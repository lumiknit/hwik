package lumiknit.app.hwik.screen.searched

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import lumiknit.app.hwik.components.TopBar
import lumiknit.app.hwik.components.articlep.BottomSheet
import lumiknit.app.hwik.core.PickerProcess
import lumiknit.app.hwik.screen.webworker.WebContext
import lumiknit.app.hwik.state.ContentsVM
import lumiknit.app.hwik.ui.theme.LocalCustomColorsPalette


/**
 * Search result item data.
 */
private data class SearchItem(
	val url: String,
	val title: String,
	val description: String? = null,
) {
	companion object {
		fun fromJsonObject(elem: JsonElement): SearchItem {
			if (elem !is JsonObject) {
				Log.e(
					"SearchedScreen",
					"SearchItem.fromJsonObject: Invalid JSON element type"
				)
				throw IllegalArgumentException("Expected a JSON object")
			}
			val jo = elem
			val url = jo["url"]?.jsonPrimitive?.content ?: ""
			val title = jo["title"]?.jsonPrimitive?.content ?: ""
			val description = jo["description"]?.jsonPrimitive?.content
			if (url.isBlank() || title.isBlank()) {
				Log.e(
					"SearchedScreen",
					"SearchItem.fromJsonObject: Invalid SearchItem JSON"
				)
				throw IllegalArgumentException(
					"SearchItem must have non-blank url and title"
				)
			}
			return SearchItem(
				url = url,
				title = title,
				description = description
			)
		}

		fun fromJsonArray(elem: JsonElement?): List<SearchItem> {
			if (elem == null || elem !is JsonArray) {
				Log.e(
					"SearchedScreen",
					"SearchItem.fromJsonArray: Invalid JSON element type"
				)
				throw IllegalArgumentException("Expected a JSON array")
			}
			return elem.toList().map {
				if (it is JsonObject) {
					SearchItem.fromJsonObject(it)
				} else {
					Log.e(
						"SearchedScreen",
						"SearchItem.fromJsonArray: Invalid item in JSON array"
					)
					throw IllegalArgumentException("Invalid item in JSON array")
				}
			}
		}
	}
}

/**
 * Load search results from the search script.
 */
private suspend fun loadURLsFromSearchScripts(
	keyword: String,
	pp: PickerProcess
): List<SearchItem> {
	Log.i("SearchedScreen", "loadFromScript: Searching for '$keyword' in scripts")
	val inputs = JsonObject(
		mapOf(
			"query" to JsonPrimitive(keyword)
		)
	)
	val result = WebContext.runScript(pp.steps, inputs)
	if (result.error != null) {
		Log.e(
			"SearchedScreen",
			"loadFromScript: Error search '$keyword': ${result.error}"
		)
		return emptyList()
	} else {
		return SearchItem.fromJsonArray(result.finalResult["\$items"])
	}
}


private suspend fun loadURLs(
	searchKeyword: String,
	onURLFound: (List<SearchItem>) -> Unit,
): List<SearchItem> {
	Log.i("SearchedScreen", "loadURLs: Searching for '$searchKeyword'")
	var jobs = mutableListOf<Deferred<Unit>>()
	for (p in ContentsVM.pickers) {
		// Check if the picker's search script is not null
		val script = p.script.search
		if (!p.searchable) {
			Log.i(
				"SearchedScreen",
				"loadURLs: Skipping picker ${p.script.id} - not searchable or no script"
			)
			continue
		}
		Log.i(
			"SearchedScreen",
			"loadURLs: Running search script for picker ${p.script.id}"
		)

		// Run the ssearch script in the new coroutine
		val job = coroutineScope {
			async {
				val items = loadURLsFromSearchScripts(searchKeyword, script)
				if (items.isNotEmpty()) {
					onURLFound(items)
				}
			}
		}
		jobs.add(job)
	}

	Log.i("SearchedScreen", "loadURLs: Total ${jobs.size} search jobs created")

	// Wait for all jobs to complete
	for (job in jobs) {
		job.await()
	}

	return emptyList()
}

@Composable
fun SearchedScreen(
	searchKeyword: String,
) {
	val searchItems = remember { mutableStateListOf<SearchItem>() }

	LaunchedEffect(searchKeyword) {
		loadURLs(searchKeyword) { items ->
			searchItems.addAll(items)
		}
	}

	Scaffold(
		modifier = Modifier
			.fillMaxSize(),
		topBar = {
			TopBar(
				title = "Search: $searchKeyword",
				onBack = true,
			)
		},
		containerColor = LocalCustomColorsPalette.current.background,
		contentColor = LocalCustomColorsPalette.current.onBackground,
	) { innerPadding ->
		Box(
			modifier = Modifier
				.fillMaxSize(),
		) {
			Column(
				modifier = Modifier
					.fillMaxSize()
					.padding(innerPadding)
					.padding(16.dp),
			) {
				for (item in searchItems) {
					Column {
						// Display each search item
						Text(
							text = item.title,
						)
						Text(
							text = item.description ?: "",
						)
						Text(
							text = item.url,
						)
					}
				}
			}

			BottomSheet(
				modifier = Modifier
					.align(Alignment.BottomCenter)
					.padding(32.dp),
				state = null,
			)
		}
	}
}