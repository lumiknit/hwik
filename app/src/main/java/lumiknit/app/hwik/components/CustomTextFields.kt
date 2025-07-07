package lumiknit.app.hwik.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import lumiknit.app.hwik.ui.theme.LocalCustomColorsPalette

@Composable
fun transparentTextFieldColors(
	containerColor: Color? = null,
): TextFieldColors {
	val containerColor =
		containerColor ?: LocalCustomColorsPalette.current.inputBackground

	return TextFieldDefaults.colors(
		unfocusedIndicatorColor = Color.Transparent,
		focusedIndicatorColor = Color.Transparent,
		errorContainerColor = Color.Transparent,
		focusedContainerColor = containerColor,
		disabledContainerColor = containerColor,
		unfocusedContainerColor = containerColor,
	)
}

@Composable
fun CustomTextField(
	modifier: Modifier = Modifier,
	value: String = "",
	onValueChange: (String) -> Unit = {},
	placeholder: @Composable (() -> Unit)? = null,
	singleLine: Boolean = false,
	maxLines: Int = Int.MAX_VALUE,
	keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
	TextField(
		modifier = modifier,
		colors = transparentTextFieldColors(),
		shape = RoundedCornerShape(8.dp),
		value = value,
		onValueChange = onValueChange,
		placeholder = placeholder,
		singleLine = singleLine,
		maxLines = maxLines,
		keyboardOptions = keyboardOptions
	)
}