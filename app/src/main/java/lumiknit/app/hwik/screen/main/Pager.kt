package lumiknit.app.hwik.screen.main

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import lumiknit.app.hwik.state.ContentsVM

// MainPager is a composable function that creates a vertical pager with the specified number of pages.
// It currently does not display any content in the pages.
@Composable
fun Pager(
	modifier: Modifier = Modifier,
	state: MainScreenState,
	onPageChange: (Int, String) -> Unit = { _i, s -> },
) {
	val pagerState = rememberPagerState(pageCount = {
		ContentsVM.articles.size
	})

	val coroutineScope = rememberCoroutineScope()

	val context = LocalContext.current

	val fling = PagerDefaults.flingBehavior(
		state = pagerState,
		snapPositionalThreshold = 0.15f
	)

	LaunchedEffect(pagerState) {
		snapshotFlow { pagerState.currentPage }.collect { page ->
			onPageChange(page, "Page $page")
		}
	}

	DisposableEffect(Unit) {
		state.pageChangeCallback = {
			coroutineScope.launch {
				when (it) {
					1 -> {
						Toast.makeText(context, "Next page", Toast.LENGTH_SHORT).show()
						pagerState.animateScrollToPage(pagerState.currentPage + 1)
					}

					-1 -> {
						pagerState.animateScrollToPage(pagerState.currentPage - 1)
					}

					0 -> {
						// Refresh action, for now, we just scroll to the current page
						pagerState.animateScrollToPage(pagerState.currentPage)
					}

					else -> {} // No action for other values
				}
			}
		}
		onDispose {
			// Cleanup if needed when the pager is disposed
			state.pageChangeCallback = {}
		}
	}

	if (pagerState.pageCount == 0) {
		CircularProgressIndicator(
			modifier = Modifier
				.fillMaxSize()
				.padding(16.dp),
			strokeWidth = 4.dp,
			color = MaterialTheme.colorScheme.primary,
			trackColor = MaterialTheme.colorScheme.surfaceVariant,
		)
	} else {
		VerticalPager(
			state = pagerState,
			flingBehavior = fling,
		) { page ->
			ArticleView(
				modifier = Modifier
					.fillMaxSize()
					.padding(4.dp, 0.dp),
				article = ContentsVM.articles[page],
			)
		}
	}
}