package lumiknit.app.hwik.comp_mainview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import lumiknit.app.hwik.MenuItem
import lumiknit.app.hwik.TopBar
import lumiknit.app.hwik.ui.theme.LocalCustomColorsPalette

/**
 * MainView is a composable function that serves as the main view of the application.
 */
@Composable
fun MainView(
	onOpenWebView: (() -> Unit)? = null,
	onOpenSources: (() -> Unit)? = null,
	onOpenPrefs: (() -> Unit)? = null,
) {
	Scaffold(
		topBar = {
			TopBar(
				title = "Main",
				menuItems = listOf(
					MenuItem(title = "Sources", onClick = {
						onOpenSources?.invoke()
					}),
					MenuItem(title = "WebView", onClick = {
						onOpenWebView?.invoke()
					}),
					MenuItem(title = "Preferences", onClick = {
						onOpenPrefs?.invoke()
					}),
				)
			)
		},
		modifier = Modifier
			.fillMaxSize(),
		containerColor = LocalCustomColorsPalette.current.background,
		contentColor = LocalCustomColorsPalette.current.onBackground,
	) { innerPadding ->
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding)
		) {
			Pager(
				modifier = Modifier
					.fillMaxSize(),
				20
			)
		}
	}
}