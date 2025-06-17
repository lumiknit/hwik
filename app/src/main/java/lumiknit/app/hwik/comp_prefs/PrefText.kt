package lumiknit.app.hwik.comp_prefs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.datastore.preferences.core.Preferences

@Composable
fun EditTextAlert(
	key: Preferences.Key<String>,
	label: String,
	description: String? = null,
	onValueChange: (String) -> String,
	onDismiss: () -> Unit,
) {
	// This function would typically show a dialog to edit the integer value.
	// For simplicity, we are not implementing the dialog here.

	val textValue = remember { mutableStateOf("") }

	Dialog(
		onDismissRequest = {
			onDismiss()
		},
		properties = DialogProperties(
			dismissOnBackPress = true,
			dismissOnClickOutside = true,
		)
	) {
		Card {
			Column(
				modifier = Modifier
					.padding(16.dp)
					.clickable { /* Handle click if needed */ }
			) {
				Text(text = label)
				if (description != null) {
					Text(text = description)
				}

				TextField(
					value = textValue.value,
					onValueChange = { newValue ->
						// Handle the value change here, e.g., save to DataStore
						textValue.value = newValue
					},
				)

				Row {
					Text(
						text = "Save",
						modifier = Modifier
							.padding(8.dp)
							.clickable {
								onValueChange(textValue.value)
								onDismiss()
							}
					)
					Text(
						text = "Cancel",
						modifier = Modifier
							.padding(8.dp)
							.clickable {
								onDismiss()
							}
					)
				}
			}
		}
	}
}

/** PrefText is a component which edit an integer preference. */
@Composable
fun PrefText(
	key: Preferences.Key<String>,
	label: String,
	description: String? = null,
) {
	val showDialog = remember { mutableStateOf(false) }

	PrefLineLabel(
		label, description,
		modifier = Modifier.fillMaxWidth(),
	) {
		showDialog.value = true
	}

	if (showDialog.value) {
		EditTextAlert(
			key = key,
			label = label,
			description = description,
			onValueChange = { newValue ->
				// Handle the value change here, e.g., save to DataStore
				newValue
			},
			onDismiss = {
				showDialog.value = false
			}
		)
	}
}