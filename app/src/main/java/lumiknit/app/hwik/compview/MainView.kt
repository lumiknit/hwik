package lumiknit.app.hwik.compview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * MainView is a composable function that serves as the main view of the application.
 */
@Composable
fun MainView() {
	Box(modifier = Modifier.fillMaxSize()) {
		Pager(
			modifier = Modifier
				.fillMaxSize(),
			20
		)
	}
}