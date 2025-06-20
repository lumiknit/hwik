package lumiknit.app.hwik.screen.main

open class MainScreenState {
	// Pager state
	// 1: next page, -1: prev page, -2: top, 0: refresh
	var pageChangeCallback: (Int) -> Unit = { }

	fun onPrevPage() {
		pageChangeCallback(-1)
	}

	open fun onNextPage() {
		pageChangeCallback(1)
	}
}