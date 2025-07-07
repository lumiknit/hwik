package lumiknit.app.hwik.screen.webworker

import android.graphics.Bitmap
import android.util.Log
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import lumiknit.app.hwik.state.GlobalVM
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private fun wrapScript(
	reqID: String,
	code: String,
	inputState: JsonObject
): String {
	val stateVar = "$"
	val retVar = "_\$ret"
	val errField = "\$error"
	val escapedReqID = '"' + reqID.replace("\"", "\\\"") + '"'
	return """
(async ($stateVar) => {
	var $retVar = $stateVar;
	try {
		$retVar = await (async () => {
		$code
	})();
	} catch (e) {
		$retVar.$errField = "Error in script: " + e;
	}
	${"\$android"}.onReturn(JSON.stringify($retVar || $stateVar), $escapedReqID)
})(${inputState}, $escapedReqID)
	"""
}

private fun filterTemporaryFields(
	state: JsonObject
) = JsonObject(state.filterKeys { !it.startsWith("$") })


// Component

private typealias RequestCallback = (String, String?) -> Unit

private object ReqManager {
	var reqIDCnt: Int = 0
	val reqCallbacks = mutableMapOf<String, RequestCallback>()
	fun issueJSReturnID(
		callback: RequestCallback
	): String {
		val now = System.currentTimeMillis()
		val reqID = "js-${now}-${reqIDCnt++}"
		reqCallbacks.put(reqID, callback)
		return reqID
	}

	//
	@JavascriptInterface
	fun onReturn(data: String, requestID: String) {
		val callback = reqCallbacks.remove(requestID)
		if (callback != null) {
			Log.d("CustomWebViewClient", "Returning data for request ID: $requestID")
			callback(data, null)
		} else {
			Log.w(
				"CustomWebViewClient",
				"No callback found for request ID: $requestID"
			)
		}
	}
}

@Composable
fun WebWorker(
	modifier: Modifier = Modifier,
	id: String = "",
) {
	val context = LocalContext.current
	val coroutineScope = rememberCoroutineScope()

	val taskDelayMS by remember { mutableLongStateOf(100L) }

	var webview by remember { mutableStateOf<WebView?>(null) }
	val pageLoadedChannel = remember {
		Channel<Unit>(1)
	}
	val cli = remember {
		object : WebViewClient() {
			override fun shouldOverrideUrlLoading(
				view: WebView?,
				request: WebResourceRequest?
			): Boolean {
				return false
			}

			override fun onPageStarted(
				view: WebView?,
				url: String?,
				favicon: Bitmap?
			) {
				super.onPageStarted(view, url, favicon)
				Log.d("WebWorker", "Page started: $url")
			}

			override fun onPageFinished(view: WebView?, url: String) {
				super.onPageFinished(view, url)
				Log.d("WebWorker", "Page finished: $url")
				val res = pageLoadedChannel.trySend(Unit)
				if (!res.isSuccess) {
					Log.w("WebWorker", "Page loaded channel is full, skipping send")
				} else {
					Log.d("WebWorker", "Page loaded channel sent successfully")
				}
			}
		}
	}

	suspend fun goToAndWait(url: String = "about:blank") {
		if (webview == null) {
			throw IllegalStateException("WebView is not initialized")
		}
		Log.d("WebWorker:goToAndWait", "Going to URL: $url")
		webview!!.loadUrl(url)
		pageLoadedChannel.receive()
		Log.d("WebWorker:goToAndWait", "Page loaded: $url")
	}

	suspend fun runJS(
		script: String,
		inputState: JsonObject
	): Pair<String, String?> {
		return suspendCoroutine { continuation ->
			val reqID = ReqManager.issueJSReturnID { result, error ->
				continuation.resume(
					Pair(result, error)
				)
			}
			val wrappedScript = wrapScript(
				reqID,
				script,
				inputState,
			)
			Log.d("WebWorker:runJS", "Start eval js[$reqID]")
			webview?.evaluateJavascript(wrappedScript) {
				Log.d("WebWorker:runJS", "End eval js[$reqID]: $it")
			}
		}
	}

	val mainStep = suspend {
		Log.d("WebWorker", "mainStep: receiving webtask")
		val task = WebContext.taskChannel.receive()
		Log.d("WebWorker", "mainStep: received webtask")

		val results = mutableListOf<WebContext.StepResult>()
		var error: String? = null

		// Reset first
		goToAndWait()

		var state = task.inputs

		for ((idx, step) in task.steps.withIndex()) {
			Log.d("WebWorker:runScriptSteps", "Step $idx: ${step.code}")

			if (idx > 0) {
				state = filterTemporaryFields(state)
				Log.d(
					"WebWorker:runScriptSteps",
					"State filtered (remove temp fields): $state"
				)
			}

			val script = wrapPickerScript(step.code)
			try {
				val (jsResult, jsError) = runJS(script, state)
				if (jsError != null) {
					error =
						"Error executing step $idx, RunError: ${jsError}"
					break
				}
				Log.d("WebWorker:runScriptSteps", "Step $idx result: ${jsResult}")

				state = buildJsonObject {
					state.forEach { (key, value) ->
						put(key, value)
					}
					try {
						val newObject = Json.decodeFromString<JsonObject>(jsResult)
						newObject.forEach { (key, value) ->
							put(key, value)
						}
					} catch (e: Exception) {
						Log.e(
							"WebWorker:runScriptSteps",
							"Error parsing JSON result from step $idx: ${e.message}"
						)
					}
				}

				// If the state has '$href' field, goToAndWait
				if (state.containsKey("\$href")) {
					val field = state["\$href"]!!
					if (!field.jsonPrimitive.isString) {
						error = "Error executing step $idx, \$href is not a string"
						break
					}
					val href = field.jsonPrimitive.content
					Log.d("WebWorker:runScriptSteps", "Step $idx href: $href")
					goToAndWait(href)
					Log.d("WebWorker:runScriptSteps", "Step $idx href done")
				}

				val sr = WebContext.StepResult(
					index = idx,
					raw = jsResult,
					state = state
				)
				task.onStepDone(idx, sr)
				results.add(sr)
			} catch (e: Exception) {
				error = "Error executing step $idx, Error: ${e.message}"
				break
			}
		}
		Log.d(
			"WebWorker:runScriptSteps",
			"Finished running steps, outputs: $results, error: $error"
		)

		// Go to about:blank to reset the webview again
		goToAndWait()

		task.onDone(
			WebContext.RunResult(
				stepResults = results,
				finalResult = state,
				error = error
			)
		)
	}

	// Controller Handling
	DisposableEffect(taskDelayMS) {
		WebContext.workerCount++

		val job = coroutineScope.launch {
			while (true) {
				try {
					mainStep()
				} catch (e: Exception) {
					e.printStackTrace()
					Toast.makeText(
						context,
						"Error in WebWorker: ${e.message}",
						Toast.LENGTH_LONG
					).show()
				}
				delay(taskDelayMS)
			}
		}

		onDispose {
			WebContext.workerCount--
			job.cancel()
		}
	}

	// Main Components
	AndroidView(
		factory = { context ->
			val wv = WebView(context)

			wv.webViewClient = cli

			GlobalVM.hdUserAgent = wv.settings.userAgentString

			wv.addJavascriptInterface(
				ReqManager,
				"\$android"
			)

			wv.settings.apply {
				javaScriptEnabled = true
				loadWithOverviewMode = true
				useWideViewPort = true
				setGeolocationEnabled(true)
				setSupportZoom(true)
				domStorageEnabled = true
				builtInZoomControls = true
				displayZoomControls = false
				allowFileAccess = true
				allowContentAccess = true
				loadsImagesAutomatically = false
			}

			wv.apply {
				layoutParams = ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT
				)
			}

			webview = wv

			wv
		},
		modifier = modifier
	)
}