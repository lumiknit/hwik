package lumiknit.app.hwik.screen.prefs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun PrefLineLabel(
	label: String,
	description: String? = null,
	modifier: Modifier = Modifier,
	onClick: (() -> Unit)? = null,
) {
	var mod = modifier
	if (onClick != null) {
		mod = mod.clickable(
			enabled = true,
			onClick = onClick,
		)
	}

	Column(
		modifier = mod,
	) {
		Text(
			text = label,
			fontWeight = FontWeight.Bold,
		)
		if (description != null) {
			Text(
				text = description,
				fontSize = 12.sp,
			)
		}
	}
}