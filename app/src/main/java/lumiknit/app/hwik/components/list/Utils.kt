package lumiknit.app.hwik.components.list

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lumiknit.app.hwik.ui.theme.listSectionTitleTextStyle

// Row, which contains bold colored text
// which show non-clickable section title (e.g. preference category)
@Composable
fun ListSectionTitle(
	label: String,
	modifier: Modifier = Modifier,
) {
	Text(
		modifier = modifier
			.fillMaxWidth()
			.padding(top = 8.dp, bottom = 4.dp),
		text = label,
		color = MaterialTheme.colorScheme.primary,
		style = listSectionTitleTextStyle
	)
}