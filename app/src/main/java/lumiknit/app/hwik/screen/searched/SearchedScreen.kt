package lumiknit.app.hwik.screen.searched

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import lumiknit.app.hwik.NavCallbacks
import lumiknit.app.hwik.components.TopBar
import lumiknit.app.hwik.components.articlep.ArticlePageState
import lumiknit.app.hwik.components.articlep.ArticlePager
import lumiknit.app.hwik.components.articlep.BottomSheet
import lumiknit.app.hwik.core.PickerProcess
import lumiknit.app.hwik.screen.webworker.WebContext
import lumiknit.app.hwik.state.ContentsVM
import lumiknit.app.hwik.ui.theme.LocalCustomColorsPalette


private suspend fun loadURLsFromSearchScripts(
	keyword: String,
	pp: PickerProcess
): List<String> {
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
		val urls =
			result.finalResult["\$urls"]?.jsonArray?.map { it.jsonPrimitive.content }
		if (urls != null) {
			Log.i(
				"SearchedScreen",
				"loadFromScript: Found ${urls.size} URLs for search '$keyword'"
			)
			return urls
		} else {
			Log.w(
				"SearchedScreen",
				"loadFromScript: No URLs found for search '$keyword'"
			)
			return emptyList()
		}
	}
}


private suspend fun loadURLs(
	searchKeyword: String,
	onURLFound: (List<String>) -> Unit,
): List<String> {
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
				val urls = loadURLsFromSearchScripts(searchKeyword, script)
				if (urls.isNotEmpty()) {
					onURLFound(urls)
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
	navCallbacks: NavCallbacks,
	searchKeyword: String,
) {
	val state = viewModel<ArticlePageState>()

	LaunchedEffect(searchKeyword) {
		loadURLs(searchKeyword) { urls ->
			state.addURLs(urls)
		}
	}

	Scaffold(
		modifier = Modifier
			.fillMaxSize(),
		topBar = {
			TopBar(
				title = "Search: $searchKeyword",
				onBack = {
					navCallbacks.onBack()
				},
			)
		},
		containerColor = LocalCustomColorsPalette.current.background,
		contentColor = LocalCustomColorsPalette.current.onBackground,
	) { innerPadding ->
		Box(
			modifier = Modifier
				.fillMaxSize(),
		) {
			ArticlePager(
				modifier = Modifier
					.fillMaxSize(),
				state,
				spaceTop = innerPadding.calculateTopPadding(),
				spaceBottom = innerPadding.calculateBottomPadding(),
			)

			BottomSheet(
				modifier = Modifier
					.align(Alignment.BottomCenter)
					.padding(32.dp),
				navCallbacks = navCallbacks,
				state = state,
			)
		}
	}
}