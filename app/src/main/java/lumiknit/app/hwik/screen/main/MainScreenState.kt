package lumiknit.app.hwik.screen.main

import lumiknit.app.hwik.state.ContentsVM

open class MainScreenState {
	var pageIndex = 0
	var totalPages = 0


	// Pager state
	// -1: next page, -2: prev page, -3: top, -4: refresh
	var pageChangeCallback: (Int) -> Unit = { }

	fun onPrevPage() {
		pageChangeCallback(-2)
	}

	open fun onNextPage() {
		pageChangeCallback(-1)
	}

	open fun onRefresh() {
		pageChangeCallback(-4)
	}

	open fun onSearch(keyword: String) {
		val keyword = keyword.trim()

		val idx = pageIndex

		ContentsVM.requestSearch(keyword, idx) {
			pageChangeCallback(idx)
		}
	}
}