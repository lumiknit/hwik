package lumiknit.app.hwik.components.articlep

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

const val GO_PREV_PAGE = -2
const val GO_NEXT_PAGE = -1

/**
 * State helper for the screen which container article
 */
class ArticlePageState : ViewModel() {
	var pageIndex = 0

	// Pager state

	var pageChangeCallback: (Int) -> Unit = { }

	fun onPrevPage() {
		Log.d("MainScreenState", "onPrevPage called, current page: $pageIndex")
		pageChangeCallback(GO_PREV_PAGE)
	}

	fun onNextPage() {
		Log.d("MainScreenState", "onNextPage called, current page: $pageIndex")
		pageChangeCallback(GO_NEXT_PAGE)
	}

	// Mutable states
	val urls = mutableStateListOf<String>()
	var currentIndex = mutableIntStateOf(0)

	fun addURLs(newUrls: List<String>) {
		urls.addAll(newUrls)
		Log.d("ArticlePageState", "Added ${newUrls.size} URLs, total: ${urls.size}")
	}
}