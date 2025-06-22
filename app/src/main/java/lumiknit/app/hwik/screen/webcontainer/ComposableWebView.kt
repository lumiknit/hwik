package lumiknit.app.hwik.screen.webcontainer

import android.util.Log
import android.view.ViewGroup
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

class CustomWebViewClient : WebViewClient() {
	override fun doUpdateVisitedHistory(
		view: WebView?,
		url: String?,
		isReload: Boolean
	) {
		super.doUpdateVisitedHistory(view, url, isReload)

		Log.d("CustomWebViewClient", "Visited URL: $url")
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
				when (t.type) {
					WebTaskType.NAV_BACK -> {
						if (!wv.canGoBack()) {
							Toast.makeText(context, "No back history", Toast.LENGTH_SHORT)
								.show()
							return@let
						}
						wv.goBack()
						t.callback?.invoke("OK", null)
					}

					WebTaskType.NAV_FORWARD -> {
						if (!wv.canGoForward()) {
							Toast.makeText(context, "No forward history", Toast.LENGTH_SHORT)
								.show()
							return@let
						}
						wv.goForward()
						t.callback?.invoke("OK", null)
					}

					WebTaskType.NAV_TO -> {
						if (t.data.isNullOrEmpty()) {
							Toast.makeText(context, "No URL provided", Toast.LENGTH_SHORT)
								.show()
							return@let
						}
						wv.loadUrl(t.data)
						t.callback?.invoke("OK", null)
					}

					WebTaskType.REFRESH -> {
						wv.reload()
						t.callback?.invoke("OK", null)
					}

					WebTaskType.EVAL_JS -> {
						if (t.data.isNullOrEmpty()) {
							Toast.makeText(
								context,
								"No JavaScript code provided",
								Toast.LENGTH_SHORT
							)
								.show()
							return@let
						}
						Log.d("ComposableWebView", "Evaluating JavaScript: ${t.data}")
						wv.evaluateJavascript(t.data) { result ->
							Log.d("ComposableWebView", "JavaScript result: $result")
							t.callback?.invoke(result ?: "", null)
						}
					}

					WebTaskType.GET_URL -> {
						val currentUrl = wv.url ?: ""
						t.callback?.invoke(currentUrl, null)
						WebControlProvider.onURLChanged(currentUrl)
					}

					else -> {
						Log.e("ComposableWebView", "Unknown task type: ${t.type}")
						Toast.makeText(
							context,
							"Unknown task type: ${t.type}",
							Toast.LENGTH_SHORT
						).show()
					}
				}
			}
		}
	}

	// Main Components
	AndroidView(
		factory = { context ->
			val wv = WebView(context)

			wv.webViewClient = CustomWebViewClient()

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
					Toast.makeText(context, "Loaded: " + result, Toast.LENGTH_LONG).show()
				}
			)
		},
		modifier = modifier
	)
}