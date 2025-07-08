package lumiknit.app.hwik.screen.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import lumiknit.app.hwik.components.articlep.ArticlePageState
import lumiknit.app.hwik.components.articlep.ArticlePager
import lumiknit.app.hwik.components.articlep.BottomSheet
import lumiknit.app.hwik.ui.theme.LocalCustomColorsPalette

/**
 * MainView is a composable function that serves as the main view of the application.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
	val coroutineScope = rememberCoroutineScope()
	val state = viewModel<ArticlePageState>()

	Scaffold(
		modifier = Modifier
			.fillMaxSize(),
		containerColor = LocalCustomColorsPalette.current.background,
		contentColor = LocalCustomColorsPalette.current.onBackground,
	) { innerPadding ->
		Box(
			modifier = Modifier
				.fillMaxSize(),
		) {
			ArticlePager(
				modifier = Modifier
					.fillMaxSize(),
				state,
				spaceTop = innerPadding.calculateTopPadding(),
				spaceBottom = innerPadding.calculateBottomPadding(),
			)

			BottomSheet(
				modifier = Modifier
					.align(Alignment.BottomCenter)
					.padding(32.dp),
				state = state,
			)
		}
	}
}