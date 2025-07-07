package lumiknit.app.hwik.components.articlep

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import lumiknit.app.hwik.state.ContentsVM

// MainPager is a composable function that creates a vertical pager with the specified number of pages.
// It currently does not display any content in the pages.
@Composable
fun ArticlePager(
	modifier: Modifier = Modifier,
	state: ArticlePageState,
	spaceTop: Dp = 0.dp,
	spaceBottom: Dp = 0.dp,
) {
	val context = LocalContext.current
	val listState = rememberLazyListState()
	val scrollState = rememberScrollState()

	DisposableEffect(Unit) {
		ContentsVM.resumstepLoop()

		state.pageChangeCallback = { idx ->
			when (idx) {
				-1 -> {
					listState.requestScrollToItem(listState.firstVisibleItemIndex + 1)
				}

				-2 -> {
					listState.requestScrollToItem(listState.firstVisibleItemIndex - 1)
				}

				-3 -> {
					// Refresh action, for now, we just scroll to the current page
					listState.requestScrollToItem(listState.firstVisibleItemIndex)
				}

				else -> {
					if (idx >= 0 && idx < ContentsVM.articles.size) {
						Toast.makeText(context, "Jump to page $idx", Toast.LENGTH_SHORT)
							.show()
						listState.requestScrollToItem(idx)
					} else {
						Log.w("Pager", "Invalid page index: $idx")
					}
				}
			}
		}
		onDispose {
			ContentsVM.pauseStepLoop()
			// Cleanup if needed when the pager is disposed
			state.pageChangeCallback = {}
		}
	}

	Column(
		modifier = Modifier.verticalScroll(state = scrollState)
	) {
		Log.i("ArticlePager", "URLs: ${state.urls.size} articles loaded")
		for (url in state.urls) {
			ArticleFetchView(
				modifier = Modifier
					.padding(
						start = 4.dp,
						end = 4.dp,
						top = spaceTop,
						bottom = spaceBottom,
					),
				url = url
			)
			HorizontalDivider(
				modifier = Modifier
					.padding(4.dp, 0.dp),
				thickness = 1.dp,
				color = MaterialTheme.colorScheme.onSurface,
			)
		}
	}
}