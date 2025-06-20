package lumiknit.app.hwik.components.modal

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

data class ConfirmModalProps(
	val title: String,
	val message: String,
	val onClick: (Boolean) -> Unit,
)

@Composable
fun ConfirmModal(
	props: ConfirmModalProps,
) {
	AlertDialog(
		title = {
			Text(text = props.title)
		},
		text = {
			Text(text = props.message)
		},
		onDismissRequest = {
			props.onClick(false) // Dismiss the dialog
		},
		confirmButton = {
			TextButton(
				onClick = {
					props.onClick(true)
				}
			) {
				Text("Confirm")
			}
		},
		dismissButton = {
			TextButton(
				onClick = {
					props.onClick(false)
				}
			) {
				Text("Cancel")
			}
		},
	)
}