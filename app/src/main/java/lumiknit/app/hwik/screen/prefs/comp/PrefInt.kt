package lumiknit.app.hwik.screen.prefs.comp

import androidx.compose.foundation.layout.Row
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
import lumiknit.app.hwik.state.ModalVM
import kotlin.math.roundToInt

/** PrefInt is a component which edit an integer preference. */
@Composable
fun PrefInt(
	key: Preferences.Key<Int>,
	label: String,
	description: String? = null,
) {
	val context = LocalContext.current
	val coroutineScope = rememberCoroutineScope()

	val n = context.dataStore.data.map {
		it[key] ?: 0
	}.collectAsState(0)

	Row {
		PrefLineLabel(
			modifier = Modifier.weight(1f),
			label = label,
			description = "$description\nCurrent: ${n.value}",
			onClick = {
				coroutineScope.launch {
					val result = ModalVM.showInputNumberModal(
						title = "Edit $label",
						message = description ?: "Enter a new value for $label",
						initialValue = n.value.toFloat(),
					)
					if (result != null) {
						context.dataStore.edit { prefs ->
							prefs[key] = result.roundToInt()
						}
					}
				}
			}
		)
	}
}