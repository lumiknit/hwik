package lumiknit.app.hwik.screen.sources

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import lumiknit.app.hwik.NavCallbacks
import lumiknit.app.hwik.components.MenuItem
import lumiknit.app.hwik.components.TopBar
import lumiknit.app.hwik.components.list.ListSectionTitle
import lumiknit.app.hwik.core.PSDatabase
import lumiknit.app.hwik.core.PSSourceEntity
import lumiknit.app.hwik.state.GlobalVM
import lumiknit.app.hwik.ui.theme.LocalCustomColorsPalette
import lumiknit.app.hwik.ui.theme.listItemDescTextStyle
import lumiknit.app.hwik.ui.theme.listItemTitleTextStyle

@Composable
fun SourceListScreen(
	navCallbacks: NavCallbacks,
) {
	val context = LocalContext.current
	val coroutineScope = rememberCoroutineScope()
	var db = PSDatabase.getDatabase(context)

	var listLoading by remember { mutableStateOf(false) }
	var sources by remember { mutableStateOf<List<PSSourceEntity>>(listOf()) }

	var loadFromDB = suspend {
		listLoading = true
		try {
			sources = db.psScriptDao().getAll()
			Toast.makeText(
				context,
				"Loaded ${sources.size} sources",
				Toast.LENGTH_SHORT
			).show()
		} finally {
			listLoading = false
		}
	}

	val deleteOrigin = { origin: PSSourceEntity ->
		coroutineScope.launch {
			db.psScriptDao().deleteById(origin.id)
			loadFromDB()
		}
	}

	LaunchedEffect(Unit) { loadFromDB() }

	Scaffold(
		topBar = {
			TopBar(
				title = "Sources",
				onBack = {
					navCallbacks.onBack()
				},
				menuItems = listOf(
					MenuItem(title = "Refresh List", onClick = {
						coroutineScope.launch {
							loadFromDB()
						}
					}),
					MenuItem(title = "Update Sources", onClick = {
						coroutineScope.launch {
							loadFromDB()
						}
					}),
				)
			)
		},
		modifier = Modifier
			.fillMaxSize(),
		containerColor = LocalCustomColorsPalette.current.background,
		contentColor = LocalCustomColorsPalette.current.onBackground,
	) { innerPadding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding)
		) {
			ListSectionTitle("Script Sources (${sources.size})")

			if (listLoading) {
				CircularProgressIndicator(
					modifier = Modifier.width(64.dp),
					color = MaterialTheme.colorScheme.secondary,
					trackColor = MaterialTheme.colorScheme.surfaceVariant,
				)
			}

			Row {
				// Add button
				Button(
					onClick = {
						navCallbacks.onRouteSourceEdit(null) // Navigate to add source screen
					}
				) {
					Icon(Icons.Default.Add, contentDescription = "Add Source")
					Text(text = "Add Source")
				}
			}

			for (src in sources) {
				PSSourceItem(
					entity = src,
					onClick = {
						navCallbacks.onRouteSourceEdit(src.id) // Navigate to edit source screen
					},
					onDelete = {
						deleteOrigin(src)
					}
				)
			}
		}
	}
}

// Items

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
			val title = entity.script.meta.getTitle()
			Text(
				if (title.isNotEmpty()) title else "Unnamed(id: ${entity.id})",
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
					if (GlobalVM.showConfirmModal(
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