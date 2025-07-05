package lumiknit.app.hwik.screen.prefs.comp

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import lumiknit.app.hwik.screen.prefs.dataStore

/** PrefBool is a component which edit an integer preference. */
@Composable
fun PrefBool(
	key: Preferences.Key<Boolean>,
	label: String,
	description: String? = null,
) {
	val context = LocalContext.current
	val coroutineScope = rememberCoroutineScope()

	val n = context.dataStore.data.map {
		it[key] ?: false
	}.collectAsState(false)

	Row {
		PrefLineLabel(
			modifier = Modifier.weight(1f),
			label = label,
			description = "$description\nCurrent: ${n.value}",
			onClick = {
				coroutineScope.launch {
					context.dataStore.edit { prefs ->
						prefs[key] = !(prefs[key] ?: false)
					}
				}
			}
		)

		Switch(
			checked = n.value,
			onCheckedChange = { checked ->
				coroutineScope.launch {
					context.dataStore.edit { prefs ->
						prefs[key] = checked
					}
				}
			},
			enabled = true,
			modifier = Modifier,
		)
	}
}