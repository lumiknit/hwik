package lumiknit.app.hwik.components.modal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import lumiknit.app.hwik.components.CustomTextField

data class InputNumberModalProps(
	val title: String,
	val message: String,
	val initialValue: Float,
	val onClick: (Float?) -> Unit,
) : ModalProps() {
	@Composable
	override fun render() {
		val props = this
		var inputNumber by remember {
			mutableStateOf(
				initialValue.toString().removeSuffix(".0")
			)
		}
		AlertDialog(
			title = {
				Text(text = props.title)
			},
			text = {
				Text(text = props.message)
				CustomTextField(
					value = inputNumber,
					onValueChange = { inputNumber = it },
					keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
				)
			},
			onDismissRequest = {
				props.onClick(null) // Dismiss the dialog
			},
			confirmButton = {
				TextButton(
					onClick = {
						val number = inputNumber.toFloatOrNull()
						props.onClick(number)
					}
				) {
					Text("Confirm")
				}
			},
			dismissButton = {
				TextButton(
					onClick = {
						props.onClick(null)
					}
				) {
					Text("Cancel")
				}
			},
		)
	}
}

data class InputTextModalProps(
	val title: String,
	val message: String,
	val initialValue: String,
	val onClick: (String?) -> Unit,
) : ModalProps() {
	@Composable
	override fun render() {
		val props = this
		var inputText by remember { mutableStateOf(initialValue) }

		AlertDialog(
			title = {
				Text(text = props.title)
			},
			text = {
				Column {
					Text(text = props.message)
					CustomTextField(
						value = inputText,
						onValueChange = { inputText = it },
					)
				}
			},
			onDismissRequest = {
				props.onClick(null) // Dismiss the dialog
			},
			confirmButton = {
				TextButton(
					onClick = {
						props.onClick(inputText)
					}
				) {
					Text("Confirm")
				}
			},
			dismissButton = {
				TextButton(
					onClick = {
						props.onClick(null)
					}
				) {
					Text("Cancel")
				}
			},
		)
	}
}