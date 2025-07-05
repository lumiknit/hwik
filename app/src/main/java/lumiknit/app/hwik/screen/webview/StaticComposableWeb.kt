package lumiknit.app.hwik.screen.webview

import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import lumiknit.app.hwik.state.GlobalVM

@Composable
fun StaticComposableWeb(
	modifier: Modifier = Modifier,
	url: String,
) {
	val coroutine = rememberCoroutineScope()

	// Main Components
	AndroidView(
		factory = { context ->
			val wv = WebView(context)

			GlobalVM.hdUserAgent = wv.settings.userAgentString

			wv.settings.apply {
				loadWithOverviewMode = true
				useWideViewPort = true
			}

			wv.apply {
				layoutParams = ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT
				)
			}
			wv
		},
		update = { wv ->
			wv.loadUrl(url)
		},
		modifier = modifier
	)
}