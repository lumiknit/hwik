package lumiknit.app.hwik.components.articlep

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import lumiknit.app.hwik.core.Article
import lumiknit.app.hwik.state.ContentsVM

@Composable
fun ArticleFetchView(
	modifier: Modifier = Modifier,
	url: String,
) {
	var article by remember { mutableStateOf<Article?>(null) }

	LaunchedEffect(Unit) {
		article = ContentsVM.fetchArticle(url)
	}

	if (article == null) {
		Text("Loading article from $url", modifier = modifier)
	} else {
		ArticleView(
			modifier = modifier,
			article = article!!
		)
	}
}