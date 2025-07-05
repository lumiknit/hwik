package lumiknit.app.hwik.screen.webworker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex

@Composable
fun WebWorkerSet(
	count: Int,
) {
	Box(
		modifier = Modifier
			.fillMaxSize()
			.zIndex(-10000f)
	) {
		for (i in 0 until count) {
			WebWorker(
				id = "web-worker-$i",
			)
		}
	}
}