package lumiknit.app.hwik.screen.prefs

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardType
import androidx.datastore.preferences.core.Preferences

/** PrefInt is a component which edit an integer preference. */
@Composable
fun PrefInt(
	key: Preferences.Key<Int>,
	label: String,
	description: String? = null,
) {
	Row {
		PrefLineLabel(
			label,
			description
		)
		// TextField for editing the integer value
		TextField(
			value = "0", // Replace with actual value from DataStore
			onValueChange = { newValue ->
			},
			label = { Text(text = "Enter value") },
			singleLine = true,
			keyboardOptions = KeyboardOptions.Default.copy(
				keyboardType = KeyboardType.Number
			)
		)
	}
}