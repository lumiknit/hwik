package lumiknit.app.hwik.screen.main

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import lumiknit.app.hwik.state.ContentsVM

// MainPager is a composable function that creates a vertical pager with the specified number of pages.
// It currently does not display any content in the pages.
@Composable
fun Pager(
	modifier: Modifier = Modifier,
	state: MainScreenState,
	onPageChange: (Int, String) -> Unit = { _i, s -> },
) {
	val context = LocalContext.current
	val listState = rememberLazyListState()
	val layoutInfo by remember { derivedStateOf { listState.layoutInfo } }

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

	if (ContentsVM.articles.isEmpty()) {
		Box(
			modifier = modifier
				.padding(8.dp)
		) {
			CircularProgressIndicator(
				modifier = Modifier
					.align(Alignment.Center)
					.size(64.dp)
					.padding(16.dp),
				strokeWidth = 4.dp,
				color = MaterialTheme.colorScheme.primary,
				trackColor = MaterialTheme.colorScheme.surfaceVariant,
			)
		}
	} else {
		LazyColumn(
			state = listState,
			modifier = modifier,
		) {
			items(items = ContentsVM.articles) { a ->
				ArticleView(
					modifier = Modifier
						.padding(4.dp, 0.dp),
					article = a,
				)

				VerticalDivider(
					modifier = Modifier
						.padding(4.dp, 0.dp),
					thickness = 1.dp,
					color = MaterialTheme.colorScheme.onSurface,
				)
			}
		}
	}
}