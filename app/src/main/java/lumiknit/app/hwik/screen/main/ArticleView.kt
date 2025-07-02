package lumiknit.app.hwik.screen.main

import android.text.format.DateUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinx.datetime.Instant
import lumiknit.app.hwik.core.Article
import lumiknit.app.hwik.core.ArticleMeta
import lumiknit.app.hwik.core.Div
import lumiknit.app.hwik.core.ImageDiv
import lumiknit.app.hwik.core.LinkSpan
import lumiknit.app.hwik.core.ParagraphDiv
import lumiknit.app.hwik.core.Span
import lumiknit.app.hwik.core.TextSpan
import lumiknit.app.hwik.core.TitleDiv
import lumiknit.app.hwik.core.VideoDiv
import lumiknit.app.hwik.core.WebImageDiv
import lumiknit.app.hwik.screen.webcontainer.StaticComposableWeb
import lumiknit.app.hwik.state.GlobalVM
import lumiknit.app.hwik.ui.theme.CustomColorsPalette
import lumiknit.app.hwik.ui.theme.LocalCustomColorsPalette
import java.text.DateFormat
import java.util.Date

private const val TITLE_1_FONT_SIZE = 28f
private const val TITLE_2_FONT_SIZE = 24f
private const val TITLE_3_FONT_SIZE = 20f
private const val TITLE_4_FONT_SIZE = 18f

private fun titleFontSize(level: Int): Float {
	return when (level) {
		1 -> TITLE_1_FONT_SIZE
		2 -> TITLE_2_FONT_SIZE
		3 -> TITLE_3_FONT_SIZE
		else -> TITLE_4_FONT_SIZE
	}
}

private fun convSpanStyle(
	spanStyle: lumiknit.app.hwik.core.SpanStyle
): SpanStyle {
	return SpanStyle(
		fontWeight = if (spanStyle.bold) FontWeight.Bold else FontWeight.Normal,
		fontStyle = if (spanStyle.italic) FontStyle.Italic else FontStyle.Normal,
		fontFamily = if (spanStyle.monospace) FontFamily.Monospace else FontFamily.SansSerif,
		textDecoration = when {
			spanStyle.underline -> TextDecoration.Underline
			spanStyle.strikeThrough -> TextDecoration.LineThrough
			else -> null
		},
	)
}

private fun compileSpan(
	colorScheme: ColorScheme,
	customPalette: CustomColorsPalette,
	spans: List<Span>
) = buildAnnotatedString {
	for (span in spans) {
		when (span) {
			is TextSpan -> {
				// Here you would apply the styles to the text.
				// For simplicity, we just append the text.
				withStyle(
					style = convSpanStyle(span.style)
				) {
					append(span.content)
				}
			}

			is LinkSpan -> {
				withStyle(
					style = SpanStyle(
						color = customPalette.linkText,
						textDecoration = TextDecoration.Underline,
					)
				) {
					withLink(LinkAnnotation.Url(span.url)) {
						append(span.content)
					}
				}
			}

			else -> {
				append("Unsupported span type: $span")
			}
		}
	}
}

@Composable
fun VideoDivView(
	modifier: Modifier = Modifier,
	div: VideoDiv,
) {
	val context = LocalContext.current
	val mediaSource = remember(div.url) {
		MediaItem.fromUri(div.url)
	}
	val exoPlayer = ExoPlayer.Builder(context).build()

	// Placeholder for video support
	// You would typically use a VideoView or similar component to display the video.
	LaunchedEffect(mediaSource) {
		exoPlayer.setMediaItem(mediaSource)
		exoPlayer.prepare()
	}

	DisposableEffect(Unit) {
		onDispose {
			exoPlayer.release()
		}
	}

	AndroidView(
		factory = { ctx ->
			PlayerView(ctx).apply {
				player = exoPlayer
				useController = true // Show controls
				keepScreenOn = true // Keep screen on while playing
			}
		},
		modifier = modifier
			.fillMaxWidth()
			.aspectRatio(16f / 9f),
	)
}

@Composable
fun DivView(
	modifier: Modifier = Modifier,
	div: Div,
) {
	when (div) {
		is TitleDiv -> {
			Text(
				modifier = Modifier
					.fillMaxWidth()
					.padding(
						vertical = 8.dp
					),
				text = div.text,
				fontSize = titleFontSize(div.level).sp,
				fontWeight = FontWeight.Bold,
			)
		}

		is ImageDiv -> {
			AsyncImage(
				modifier = Modifier
					.fillMaxWidth(),
				model = ImageRequest.Builder(LocalContext.current).data(div.url)
					.diskCacheKey(div.url)
					.diskCachePolicy(CachePolicy.ENABLED)
					.crossfade(true)
					.httpHeaders(
						NetworkHeaders.Builder().set("User-Agent", GlobalVM.hdUserAgent)
							.build()
					)
					.build(),
				imageLoader = GlobalVM.setUpImageLoader(LocalContext.current),
				contentDescription = div.alt,
				contentScale = if (div.fillWidth) ContentScale.FillWidth else ContentScale.Fit,
			)
		}

		is WebImageDiv -> {
			StaticComposableWeb(modifier = Modifier, div.url)
		}

		is VideoDiv -> {
			VideoDivView(
				modifier = modifier.fillMaxWidth(),
				div = div,
			)
		}

		is ParagraphDiv -> {
			val colorScheme = MaterialTheme.colorScheme

			Text(
				modifier = modifier,
				text = compileSpan(
					colorScheme = colorScheme,
					customPalette = LocalCustomColorsPalette.current,
					div.content
				),
				color = colorScheme.onSurface,
			)
		}

		else -> {
			Text(text = "Unsupported div type: $div")
		}
	}
}

fun Instant.asTimeOrRecentDate(): String =
	DateUtils.formatSameDayTime(
		this.toEpochMilliseconds(),
		Date().time,
		DateFormat.LONG,
		DateFormat.SHORT
	).toString()

@Composable
fun ArticleMetaView(
	modifier: Modifier = Modifier,
	meta: ArticleMeta,
) {
	val uriHandler = LocalUriHandler.current
	var showDetails by remember { mutableStateOf(false) }

	Column(modifier = modifier) {
		Text(
			text = meta.title,
			fontSize = TITLE_1_FONT_SIZE.sp,
			lineHeight = TITLE_1_FONT_SIZE.sp * 1.2f,
			fontWeight = FontWeight.ExtraBold,
			modifier = Modifier
				.fillMaxWidth()
				.clickable(
					enabled = true,
					onClick = { showDetails = !showDetails }
				)
		)
		Text(
			text = "${meta.date?.asTimeOrRecentDate()}",
			fontSize = 14.sp,
		)
		if (showDetails) {
			Text(
				modifier = Modifier.clickable(
					enabled = meta.href.isNotEmpty(),
					onClick = { uriHandler.openUri(meta.href) }
				),
				text = "URL: ${meta.href}",
				textDecoration = TextDecoration.Underline,
				fontSize = 14.sp,
			)
			Text(
				text = "Author: ${meta.author ?: "Unknown"}",
				fontSize = 14.sp,
			)
			Text(
				text = "Description: ${meta.description ?: "No description available."}",
				fontSize = 14.sp,
			)
		}
		HorizontalDivider(
			modifier = Modifier.padding(vertical = 6.dp),
			thickness = 1.dp,
			color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
		)
	}
}

@Composable
fun ArticleView(
	modifier: Modifier = Modifier,
	article: Article,
) {
	SelectionContainer(modifier) {
		Column(
			modifier = modifier
		) {
			// Meta field
			ArticleMetaView(
				meta = article.meta,
			)

			// Main content
			for (div in article.content) {
				DivView(
					div = div,
				)
			}

			// Bottom padding.
			Spacer(
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 32.dp)
			)
		}
	}
}