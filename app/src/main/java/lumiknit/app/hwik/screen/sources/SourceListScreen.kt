package lumiknit.app.hwik.screen.sources

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import lumiknit.app.hwik.ui.theme.LocalCustomColorsPalette

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
