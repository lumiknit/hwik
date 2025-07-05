package lumiknit.app.hwik.screen.webview

import android.graphics.Bitmap
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
import lumiknit.app.hwik.state.GlobalVM

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
			// Pop from the WebControlProvider
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

			GlobalVM.hdUserAgent = wv.settings.userAgentString

			wv.webViewClient = cli

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
		update = { wv ->
			wv.loadUrl(url)

			wv.evaluateJavascript(
				"""
					// Get the title of the page
					document.title;
				""".trimIndent()
			) { result ->
				Toast.makeText(context, "Loaded: $result", Toast.LENGTH_LONG).show()
			}
		},
		modifier = modifier
	)
}