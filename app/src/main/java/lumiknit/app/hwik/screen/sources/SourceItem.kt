package lumiknit.app.hwik.screen.sources

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import lumiknit.app.hwik.core.PSDatabase
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
	val db =
		remember { PSDatabase.getDatabase(context) } // Remember the DB instance

	var enabled by remember { mutableStateOf(true) }

	LaunchedEffect(entity) {
		enabled = entity.enabled
	}

	Row(
		modifier = Modifier
			.padding(
				horizontal = 8.dp,
				vertical = 4.dp
			)
	) {
		Checkbox(
			checked = enabled,
			onCheckedChange = {
				coroutineScope.launch {
					db.psScriptDao().update(entity.copy(enabled = it))
					enabled = it
				}
			},
		)

		Column(
			modifier = Modifier
				.weight(1f)
				.clickable(onClick = onClick)
		) {
			Text(
				if (entity.script.id.isEmpty()) "Unnamed(${entity.id})" else entity.script.id,
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