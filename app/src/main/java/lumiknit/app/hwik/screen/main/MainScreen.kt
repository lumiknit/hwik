package lumiknit.app.hwik.screen.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import lumiknit.app.hwik.NavCallbacks
import lumiknit.app.hwik.components.MenuItem
import lumiknit.app.hwik.components.TopBar
import lumiknit.app.hwik.ui.theme.LocalCustomColorsPalette

/**
 * MainView is a composable function that serves as the main view of the application.
 */
@Composable
fun MainScreen(
	navCallbacks: NavCallbacks,
) {
	val state = object : MainScreenState() {}
	var title by remember { mutableStateOf("") }

	Scaffold(
		topBar = {
			TopBar(
				title = title,
				menuItems = listOf(
					MenuItem(title = "Sources", onClick = {
						navCallbacks.onRouteSourceList()
					}),
					MenuItem(title = "WebView", onClick = {
						navCallbacks.onRouteWebShowView()
					}),
					MenuItem(title = "Preferences", onClick = {
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
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding)
		) {
			Pager(
				modifier = Modifier
					.fillMaxSize(),
				state,
				onPageChange = { _i, newTitle ->
					title = newTitle
				},
			)

			FloatingButton(
				modifier = Modifier.align(Alignment.BottomCenter),
				state = state,
			)
		}
	}
}