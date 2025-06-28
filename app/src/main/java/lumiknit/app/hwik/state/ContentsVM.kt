package lumiknit.app.hwik.state

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import lumiknit.app.hwik.core.PSSourceEntity
import lumiknit.app.hwik.core.PickerScript
import lumiknit.app.hwik.screen.webcontainer.WebScriptRunner

data class FetchedResult(
	val url: String,
)

data class ContentsPicker(
	val script: PickerScript,
	val urlRE: Regex,
	val searchable: Boolean,
)

data class ArticleSearchRequest(
	val query: String,
	val insertPage: Int = 0,
	val onFinish: () -> Unit = {}
)

object ContentsVM : ViewModel() {
	var pickers by mutableStateOf<List<ContentsPicker>>(listOf())

	var nextListFetch: Int = 0

	val fetchedArticleURLs = mutableStateListOf<FetchedResult>()

	val articles = mutableStateListOf<Article>()

	var requests = mutableListOf<ArticleSearchRequest>()

	fun requestSearch(
		query: String,
		insertPage: Int = 0,
		onFinish: () -> Unit = {}
	) {
		val request = ArticleSearchRequest(query, insertPage, onFinish)
		requests.add(request)
		Log.i("ContentsVM", "Search request added: $query")
	}

	fun loadScriptsFromSources(
		entities: List<PSSourceEntity>
	) {
		pickers = entities.map { entity ->
			ContentsPicker(
				script = entity.script,
				urlRE = Regex(entity.script.urlRE),
				searchable = entity.script.search.steps.isNotEmpty()
			)
		}
		nextListFetch = 0
		Log.i("ContentsVM", "Scripts loaded from sources: ${pickers.size} pickers")
	}

	suspend fun loadScriptsFromDB(context: Context) {
		val db = PSDatabase.getDatabase(context)
		val entities = db.psScriptDao().getAll()
		loadScriptsFromSources(entities)
	}

	private fun removeRandomFetched(): FetchedResult? {
		if (fetchedArticleURLs.isEmpty()) return null
		val randomIndex = (0 until fetchedArticleURLs.size).random()
		return fetchedArticleURLs.removeAt(randomIndex)
	}

	suspend fun fetchList(): Boolean {
		if (pickers.isEmpty()) {
			Log.e("ContentsVM", "No pickers available to fetch lists")
			return false
		}

		val picker = pickers[nextListFetch]
		val script = picker.script
		nextListFetch = (nextListFetch + 1) % pickers.size
		Log.i("ContentsVM", "Fetching list using script: ${script.id}")

		val result =
			WebScriptRunner.runScriptSteps(
				script.articleList.steps,
				JsonObject(emptyMap())
			)
		if (result.error != null) {
			Log.e("ContentsVM", "Error fetching list: ${result.error}")
			return false
		}
		try {
			val rawURLs = result.finalResult["\$urls"]?.jsonArray
			if (rawURLs == null || rawURLs.isEmpty()) {
				Log.e("ContentsVM", "No URLs found in the result")
				return false
			}
			rawURLs.forEach { e ->
				if (e.jsonPrimitive.isString && e.jsonPrimitive.content.isNotBlank()) {
					fetchedArticleURLs.add(
						FetchedResult(
							url = e.jsonPrimitive.content
						)
					)
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
		val picker = pickers.find {
			Log.d("ContentsVM", "URL $url with re ${it.urlRE.pattern}")
			it.urlRE.find(url) != null
		}
		if (picker == null) {
			Log.e("ContentsVM", "No picker found for URL: $url")
			return null
		}

		val script = picker.script
		val result =
			WebScriptRunner.runScriptSteps(
				script.articleContent.steps,
				JsonObject(mapOf("url" to JsonPrimitive(url)))
			)
		if (result.error != null) {
			Log.e("ContentsVM", "Error fetching article: ${result.error}")
			return null
		}

		try {
			val articleField = result.finalResult["\$article"]
			val article = Article.fromJSON(Json.encodeToString(articleField))
			return article
		} catch (e: Exception) {
			Log.e("ContentsVM", "Error parsing article: $e")
			return null
		}
	}

	suspend fun handleSearchRequest() {
		if (requests.isEmpty()) {
			Log.i("ContentsVM", "No search requests to handle")
			return
		}

		val request = requests.removeAt(0)
		Log.i("ContentsVM", "Handling search request: ${request.query}")

		// Check if the request is an URL
		if (request.query.startsWith("http://") || request.query.startsWith("https://")) {
			Log.i("ContentsVM", "Request is a URL, fetching article directly")
			val article = fetchArticle(request.query)
			if (article != null) {
				articles.add(request.insertPage, article)
			} else {
				Log.e("ContentsVM", "Failed to fetch article for URL: ${request.query}")
			}
			request.onFinish()
			return
		}

		// Otherwise, treat it as a search query
		// TODO: Implement search functionality
	}

	suspend fun step(
		contentsEnough: Boolean = false
	) {

		// First of all, check if there are some requests
		if (requests.isNotEmpty()) {
			Log.i("ContentsVM", "Handling search request from queue")
			handleSearchRequest()
			return
		}

		if (contentsEnough)
			return

		Log.i("ContentsVM", "Start to fetch more articles")

		var popped = removeRandomFetched()
		var retries = 10
		while (retries > 0 && popped == null) {
			// Fetch more lists
			fetchList()
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
		articles.add(article)
	}
}