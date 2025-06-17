package lumiknit.app.hwik

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import lumiknit.app.hwik.comp_mainview.MainView
import lumiknit.app.hwik.comp_prefs.PreferencesView
import lumiknit.app.hwik.comp_sources.SourceListView
import lumiknit.app.hwik.comp_webview.WebShowView

@Composable
fun RootView() {
	var showSources by remember { mutableStateOf(false) }
	var showPreferences by remember { mutableStateOf(false) }
	var showWebView by remember { mutableStateOf(false) }

	Box {
		MainView(
			onOpenSources = {
				showSources = true
			},
			onOpenWebView = {
				showWebView = true
			},
			onOpenPrefs = {
				showPreferences = true
			}
		)

		// WebShowView should not be unmounted when it is not visible
		// because webview should be running in the background
		WebShowView(
			modifier = Modifier.zIndex(
				if (showWebView) 1f else -1f
			),
			url = "https://www.naver.com",
			onClose = {
				showWebView = false
			}
		)

		if (showSources) {
			SourceListView(
				onClose = {
					showSources = false
				}
			)
		}

		if (showPreferences) {
			PreferencesView(
				onClose = { showPreferences = false }
			)
		}
	}
}