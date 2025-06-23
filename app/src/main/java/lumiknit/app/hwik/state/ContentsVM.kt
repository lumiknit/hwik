package lumiknit.app.hwik.state

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import lumiknit.app.hwik.core.Article
import lumiknit.app.hwik.core.PSDatabase
import lumiknit.app.hwik.core.PickerScript
import lumiknit.app.hwik.screen.webcontainer.WebController

data class FetchedResult(
	val url: String,
)

data class ContentsPicker(
	val script: PickerScript,
	val urlRE: Regex,
	val searchable: Boolean,
)

object ContentsVM : ViewModel() {
	var pickers by mutableStateOf<List<ContentsPicker>>(listOf())

	var nextListFetch: Int = 0

	var fetchedArticleURLs by mutableStateOf<List<FetchedResult>>(listOf())

	var articles by mutableStateOf<List<Article>>(listOf())

	suspend fun loadScriptsFromDB(context: Context) {
		val db = PSDatabase.getDatabase(context)
		pickers = db.psScriptDao().getAll().map {
			ContentsPicker(
				script = it.script,
				urlRE = Regex(it.script.urlRE),
				searchable = it.script.search.steps.isNotEmpty()
			)
		}
		nextListFetch = 0
	}

	private fun removeRandomFetched(): FetchedResult? {
		if (fetchedArticleURLs.isEmpty()) return null
		val randomIndex = (0 until fetchedArticleURLs.size).random()
		val removed = fetchedArticleURLs[randomIndex]
		fetchedArticleURLs =
			fetchedArticleURLs.toMutableList().apply { removeAt(randomIndex) }
		return removed
	}

	suspend fun fetchList(): Boolean {
		val script = pickers[nextListFetch].script
		nextListFetch = (nextListFetch + 1) % pickers.size

		val result =
			WebController.runScriptSteps(
				script.articleList.steps,
				JsonObject(emptyMap())
			)
		if (result.error != null) {
			Log.e("ContentsVM", "Error fetching list: ${result.error}")
			return false
		}
		try {
			val rawURLs = result.finalResult["urls"]?.jsonArray
			if (rawURLs == null || rawURLs.isEmpty()) {
				Log.e("ContentsVM", "No URLs found in the result")
				return false
			}
			fetchedArticleURLs = fetchedArticleURLs.toMutableList().apply {
				rawURLs.forEach { e ->
					if (e.jsonPrimitive.isString && e.jsonPrimitive.content.isNotBlank()) {
						add(
							FetchedResult(
								url = e.jsonPrimitive.content
							)
						)
					}
				}
			}
			return true
		} catch (e: Exception) {
			Log.e("ContentsVM", "Error parsing URLs: $e")
			return false
		}
	}

	suspend fun fetchArticle(url: String): Article? {
		// Find the picker that matches the URL
		val picker = pickers.find { it.urlRE.matches(url) }
		if (picker == null) {
			Log.e("ContentsVM", "No picker found for URL: $url")
			return null
		}

		val script = picker.script
		val result =
			WebController.runScriptSteps(
				script.articleContent.steps,
				JsonObject(mapOf("url" to JsonPrimitive(url)))
			)
		if (result.error != null) {
			Log.e("ContentsVM", "Error fetching article: ${result.error}")
			return null
		}

		try {
			val articleField = result.finalResult["article"]
			val article = Article.fromJSON(Json.encodeToString(articleField))
			return article
		} catch (e: Exception) {
			Log.e("ContentsVM", "Error parsing article: $e")
			return null
		}
	}

	suspend fun setNextArticle() {
		var popped = removeRandomFetched()
		var retries = 10
		while (retries > 0 && popped == null) {
			// Fetch more lists
			if (!fetchList())
				return
			popped = removeRandomFetched()
			retries--
		}

		if (popped == null) {
			Log.e("ContentsVM", "No more articles to fetch")
			return
		}

		// Now we have a URL to fetch
		val article = fetchArticle(popped.url)
		if (article == null) {
			Log.e("ContentsVM", "Failed to fetch article for URL: ${popped.url}")
			return
		}
		// Add to articles list
		articles = articles.toMutableList().apply {
			add(article)
		}
	}
}