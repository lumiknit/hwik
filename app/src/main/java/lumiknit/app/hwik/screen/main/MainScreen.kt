package lumiknit.app.hwik.screen.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import lumiknit.app.hwik.NavCallbacks
import lumiknit.app.hwik.R
import lumiknit.app.hwik.components.MenuItem
import lumiknit.app.hwik.components.TopBar
import lumiknit.app.hwik.state.ContentsVM
import lumiknit.app.hwik.ui.theme.LocalCustomColorsPalette

/**
 * MainView is a composable function that serves as the main view of the application.
 */
@Composable
fun MainScreen(
	navCallbacks: NavCallbacks,
) {
	val state by remember { mutableStateOf(object : MainScreenState() {}) }

	var pageIndex by remember { mutableStateOf(0) }
	var pageTitle by remember { mutableStateOf("") }

	var title = "(${1 + pageIndex} / ${ContentsVM.articles.size}) $pageTitle"

	Scaffold(
		topBar = {
			TopBar(
				title = title,
				menuItems = listOf(
					MenuItem(
						title = "${stringResource(R.string.dd_menu_sources)}(${ContentsVM.pickers.size})",
						onClick = {
							navCallbacks.onRouteSourceList()
						}),
					MenuItem(title = stringResource(R.string.dd_menu_webview), onClick = {
						navCallbacks.onRouteWebShowView()
					}),
					MenuItem(
						title = stringResource(R.string.dd_menu_preferences),
						onClick = {
							navCallbacks.onRoutePreferences()
						}),
				)
			)
		},
		modifier = Modifier
			.fillMaxSize(),
		containerColor = LocalCustomColorsPalette.current.background,
		contentColor = LocalCustomColorsPalette.current.onBackground,
	) { innerPadding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding)
		) {
			Pager(
				modifier = Modifier
					.fillMaxWidth()
					.weight(1f),
				state,
				onPageChange = { pgIdx, pgTitle ->
					pageIndex = pgIdx
					pageTitle = pgTitle
				},
			)

			BottomButtons(
				modifier = Modifier.fillMaxWidth(),
				state = state,
			)
		}
	}
}