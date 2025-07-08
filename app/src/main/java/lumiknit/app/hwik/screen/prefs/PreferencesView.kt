package lumiknit.app.hwik.screen.prefs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import lumiknit.app.hwik.components.TopBar
import lumiknit.app.hwik.screen.prefs.comp.PrefBool
import lumiknit.app.hwik.screen.prefs.comp.PrefInt
import lumiknit.app.hwik.screen.prefs.comp.PrefText
import lumiknit.app.hwik.ui.theme.LocalCustomColorsPalette

@Composable
fun PreferencesView() {
	// Preference Screen
	val context = LocalContext.current

	Scaffold(
		topBar = {
			TopBar(
				title = "Preferences",
				onBack = true
			)
		},
		modifier = Modifier
			.fillMaxSize(),
		containerColor = LocalCustomColorsPalette.current.background,
		contentColor = LocalCustomColorsPalette.current.onBackground,
	) { innerPadding ->
		Box(modifier = Modifier.padding(innerPadding)) {
			Column(
				modifier = Modifier.padding(16.dp),
				verticalArrangement = Arrangement.spacedBy(10.dp)
			) {
				PrefInt(
					UserPrefs.EXAMPLE_COUNTER,
					"Counter",
					"Example counter preference",
				)
				PrefText(
					UserPrefs.EXAMPLE_CODE,
					"Text",
					"Example text preference",
				)
				PrefBool(
					UserPrefs.EXAMPLE_SWITCH,
					"Switch",
					"Example switch preference",
				)
			}
		}
	}
}