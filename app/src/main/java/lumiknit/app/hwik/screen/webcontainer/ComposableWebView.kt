package lumiknit.app.hwik.screen.webcontainer

import android.graphics.Bitmap
import android.util.Log
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.serialization.json.JsonObject

typealias RequestCallback = (String, String?) -> Unit

class CustomWebViewClient : WebViewClient() {
	override fun doUpdateVisitedHistory(
		view: WebView?,
		url: String?,
		isReload: Boolean
	) {
		super.doUpdateVisitedHistory(view, url, isReload)

		Log.d("CustomWebViewClient", "URL Changed: $url")
		WebControlProvider.onURLChanged(url)
	}

	override fun shouldOverrideUrlLoading(
		view: WebView,
		request: WebResourceRequest?
	): Boolean {
		view.loadUrl(request?.url.toString())
		Log.d("CustomWebViewClient", "Loading URL: ${request?.url}")
		WebControlProvider.onURLChanged(request?.url.toString())
		return true // Return true to indicate that we handled the URL loading
	}

	override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
		super.onPageStarted(view, url, favicon)
		Log.d("CustomWebViewClient", "Page started: $url")
		WebControlProvider.onPageStarted()
	}

	override fun onPageFinished(view: WebView?, url: String) {
		super.onPageFinished(view, url)
		Log.d("CustomWebViewClient", "Page finished: $url")
		WebControlProvider.onPageFinished(url)
	}
}

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

fun wrapScript(
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

@Composable
fun ComposableWebView(
	modifier: Modifier = Modifier,
	url: String,
) {
	val context = LocalContext.current

	val taskDelayMS by remember { mutableLongStateOf(100L) }

	var webview by remember { mutableStateOf<WebView?>(null) }

	// Controller Handling
	LaunchedEffect(taskDelayMS) {
		WebControlProvider.connect()

		while (true) {
			// Pop from the webcontrolprovider
			val t = WebControlProvider.popTask()

			webview?.let { wv ->
				val tt = t.type
				when (tt) {
					is WebTaskNavBack -> {
						if (!wv.canGoBack()) {
							Toast.makeText(context, "No back history", Toast.LENGTH_SHORT)
								.show()
							return@let
						}
						wv.goBack()
						t.callback?.invoke("OK", null)
					}

					is WebTaskNavForward -> {
						if (!wv.canGoForward()) {
							Toast.makeText(context, "No forward history", Toast.LENGTH_SHORT)
								.show()
							return@let
						}
						wv.goForward()
						t.callback?.invoke("OK", null)
					}

					is WebTaskNavTo -> {
						wv.loadUrl(tt.url)
						t.callback?.invoke("OK", null)
					}

					is WebTaskRefresh -> {
						wv.reload()
						t.callback?.invoke("OK", null)
					}

					is WebTaskEvalJS -> {
						val reqID = ReqManager.issueJSReturnID(t.callback ?: { _, _ -> })
						val wrappedScript = wrapScript(
							reqID,
							tt.script,
							tt.state
						)

						Log.d("ComposableWebView", "Start eval js[$reqID]: ${tt.script}")
						wv.evaluateJavascript(wrappedScript) {
							Log.d("ComposableWebView", "End eval js[$reqID]: $it")
						}
					}

					is WebTaskGetURL -> {
						val currentUrl = wv.url ?: ""
						t.callback?.invoke(currentUrl, null)
						WebControlProvider.onURLChanged(currentUrl)
					}
				}
			}
		}
	}

	// Main Components
	AndroidView(
		factory = { context ->
			val wv = WebView(context)
			val cli = CustomWebViewClient()

			wv.webViewClient = cli
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
		update = { wv ->
			wv.loadUrl(url)

			wv.evaluateJavascript(
				"""
					// Get the title of the page
					document.title;
				""".trimIndent(),
				{ result ->
					Toast.makeText(context, "Loaded: $result", Toast.LENGTH_LONG).show()
				}
			)
		},
		modifier = modifier
	)
}