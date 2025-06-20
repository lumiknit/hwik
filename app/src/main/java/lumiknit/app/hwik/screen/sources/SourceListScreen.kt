package lumiknit.app.hwik.screen.sources

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import lumiknit.app.hwik.NavCallbacks
import lumiknit.app.hwik.components.MenuItem
import lumiknit.app.hwik.components.TopBar
import lumiknit.app.hwik.components.list.ListSectionTitle
import lumiknit.app.hwik.core.SSItemEntity
import lumiknit.app.hwik.core.SSOriginEntity
import lumiknit.app.hwik.core.SourceScriptDatabase
import lumiknit.app.hwik.ui.theme.LocalCustomColorsPalette

@Composable
fun SourceListScreen(
	navCallbacks: NavCallbacks,
) {
	val context = LocalContext.current
	val coroutineScope = rememberCoroutineScope()
	var db = SourceScriptDatabase.getDatabase(context)

	var sourceOrigins by remember { mutableStateOf<List<SSOriginEntity>>(listOf()) }
	var sourceItems by remember { mutableStateOf<List<SSItemEntity>>(listOf()) }

	var loadFromDB = suspend {
		sourceOrigins = db.ssOriginDao().getAll()
		sourceItems = db.ssScriptDao().getAll()
	}

	val addRandOrigin = {
		coroutineScope.launch {
			val newOrigin = SSOriginEntity(
				lastFetched = Clock.System.now(),
				url = "Hello akjsdklajsldkjaskldjalksjdlaksjdklasjdklajslkdjaksldjaklsdjalksjdlaksjdlk"
			)
			db.ssOriginDao().insert(newOrigin)
			loadFromDB()
		}
		Unit
	}

	val deleteOrigin = { origin: SSOriginEntity ->
		coroutineScope.launch {
			db.ssOriginDao().deleteById(origin.id)
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
					MenuItem(title = "Sources", onClick = {
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
			ListSectionTitle("Script Origins (${sourceOrigins.size})")

			Row {
				// Add button
				Button(
					onClick = addRandOrigin
				) {
					Text(text = "From URL")
				}

				Button(
					onClick = {}
				) {
					Text(text = "From JSON")
				}
			}

			for (origin in sourceOrigins) {
				SSOriginItem(
					entity = origin,
					onClick = {},
					onDelete = {
						deleteOrigin(origin)
					}
				)
			}

			ListSectionTitle("Sources (${sourceItems.size})")
			Row {
				// Add button
				Button(
					onClick = addRandOrigin
				) {
					Text(text = "Reload All")
				}
			}

			for (item in sourceItems) {
				SSItem(
					entity = item,
					origin = sourceOrigins.firstOrNull { it.id == item.originID },
					onClick = {}
				)
			}
		}
	}
}
