package lumiknit.app.hwik.comp_sources

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import lumiknit.app.hwik.MenuItem
import lumiknit.app.hwik.TopBar
import lumiknit.app.hwik.ui.theme.LocalCustomColorsPalette

@Composable
fun SourceListView(
	onClose: (() -> Unit)? = null,
) {
	Scaffold(
		topBar = {
			TopBar(
				title = "Sources",
				onBack = onClose,
				menuItems = listOf(
					MenuItem(title = "Sources", onClick = {
					}),
				)
			)
		},
		modifier = Modifier
			.fillMaxSize(),
		containerColor = LocalCustomColorsPalette.current.background,
		contentColor = LocalCustomColorsPalette.current.onBackground,
	) { innerPadding ->
		Column(modifier = Modifier
			.fillMaxSize()
			.padding(innerPadding)) {
			Text("asd")
		}
	}
}
