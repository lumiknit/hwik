package lumiknit.app.hwik.screen.sources

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import lumiknit.app.hwik.core.PSSourceEntity
import lumiknit.app.hwik.state.GlobalStore
import lumiknit.app.hwik.ui.theme.listItemDescTextStyle
import lumiknit.app.hwik.ui.theme.listItemTitleTextStyle

@Composable
fun PSSourceItem(
	entity: PSSourceEntity,
	onClick: () -> Unit,
	onDelete: () -> Unit,
) {
	val coroutineScope = rememberCoroutineScope()
	val context = LocalContext.current

	Row(
		modifier = Modifier
			.padding(
				horizontal = 8.dp,
				vertical = 4.dp
			)
	) {
		Icon(
			if (entity.url == null) Icons.Default.Email else Icons.Default.Share,
			contentDescription = "Source Icon",
			modifier = Modifier
				.size(40.dp)
				.padding(end = 16.dp, top = 8.dp, bottom = 8.dp)
				.clickable(onClick = onClick)
		)

		Column(
			modifier = Modifier
				.weight(1f)
				.clickable(onClick = onClick)
		) {
			Text(
				entity.id,
				style = listItemTitleTextStyle
			)
			Text(
				entity.url ?: "No URL",
				style = listItemDescTextStyle
			)
			Text(
				"Last fetched: ${entity.lastFetched}",
				style = listItemDescTextStyle
			)
		}
		IconButton(
			onClick = {
				coroutineScope.launch {
					if (GlobalStore.showConfirmModal(
							"Delete Source",
							"Are you sure you want to delete this source?"
						)
					) {
						onDelete()
						Toast.makeText(context, "Origin deleted", Toast.LENGTH_SHORT).show()
					}
				}
			},
		) {
			Icon(Icons.Default.Delete, contentDescription = "Delete Source")
		}
	}
}