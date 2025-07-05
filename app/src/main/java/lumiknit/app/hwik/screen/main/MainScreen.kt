package lumiknit.app.hwik.screen.main

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import lumiknit.app.hwik.NavCallbacks
import lumiknit.app.hwik.R
import lumiknit.app.hwik.components.MenuItem
import lumiknit.app.hwik.components.TopBar
import lumiknit.app.hwik.state.ContentsVM
import lumiknit.app.hwik.state.ModalVM
import lumiknit.app.hwik.ui.theme.LocalCustomColorsPalette

/**
 * MainView is a composable function that serves as the main view of the application.
 */
@Composable
fun MainScreen(
	navCallbacks: NavCallbacks,
) {
	val context = LocalContext.current
	val coroutineScope = rememberCoroutineScope()

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
					MenuItem(
						title = "Test",
						onClick = {
							coroutineScope.launch {
								val result = ModalVM.showInputTextModal(
									"Test Input",
									"Enter some text for testing",
								)

								if (result != null) {
									Toast.makeText(
										context,
										"Input received: $result",
										Toast.LENGTH_SHORT
									).show()
								} else {
									Toast.makeText(
										context,
										"Input cancelled",
										Toast.LENGTH_SHORT
									).show()
								}
							}
						}
					),
					MenuItem(
						title = "TestNum",
						onClick = {
							coroutineScope.launch {
								val result = ModalVM.showInputNumberModal(
									"Test Num",
									"Enter some text for testing",
								)

								if (result != null) {
									Toast.makeText(
										context,
										"Input received: $result",
										Toast.LENGTH_SHORT
									).show()
								} else {
									Toast.makeText(
										context,
										"Input cancelled",
										Toast.LENGTH_SHORT
									).show()
								}
							}
						}
					)
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