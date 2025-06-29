package lumiknit.app.hwik.core

import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
sealed interface Div {}

@Serializable
@SerialName("title")
data class TitleDiv(
	val text: String,
	val level: Int = 1,
) : Div {
	override fun toString(): String {
		return "Title(text='$text', level=$level)"
	}
}

@Serializable
@SerialName("image")
data class ImageDiv(
	var url: String,
	var alt: String = "",
	var fillWidth: Boolean = false,
) : Div {
	override fun toString(): String {
		return "Image(url='$url', alt='$alt', fillWidth=$fillWidth)"
	}
}

@Serializable
@SerialName("webImage")
data class WebImageDiv(
	var url: String,
	var alt: String = "",
) : Div {
	override fun toString(): String {
		return "WebImage(url='$url', alt='$alt')"
	}
}

@Serializable
@SerialName("video")
data class VideoDiv(
	var url: String,
	var alt: String = "",
) : Div {
	override fun toString(): String {
		return "Video(url='$url', alt='$alt')"
	}
}

@Serializable
@SerialName("p")
data class ParagraphDiv(
	var content: List<Span>,
) : Div {
	override fun toString(): String {
		return "Paragraph(content=$content)"
	}
}

@Serializable
sealed interface Span {}

@Serializable
data class SpanStyle(
	var bold: Boolean = false,
	var italic: Boolean = false,
	var underline: Boolean = false,
	var strikeThrough: Boolean = false,
	var monospace: Boolean = false,
	var fgColor: String? = null, // Color for light mode.
	var bgColor: String? = null, // Background color for light mode.
) {
	override fun toString(): String {
		return "SpanStyle(bold=$bold, italic=$italic, underline=$underline, strikeThrough=$strikeThrough, fgLight=$fgColor, bgLight=$bgColor)"
	}
}

@Serializable
@SerialName("text")
data class TextSpan(
	var content: String,
	var style: SpanStyle = SpanStyle(),
) : Span {
	override fun toString(): String {
		return "TextSpan(text='$content', style=$style)"
	}
}

@Serializable
@SerialName("link")
data class LinkSpan(
	var content: String,
	var url: String,
	var style: SpanStyle = SpanStyle(),
) : Span {
	override fun toString(): String {
		return "TextSpan(text='$content', url=$url, style=$style)"
	}
}

@Serializable
data class ArticleMeta(
	var href: String, // URL to the article or source.
	var title: String,
	var author: String? = null,
	@Contextual()
	var date: Instant? = null,
	var tags: List<String> = emptyList(),
	var description: String? = null,

	@Contextual()
	var fetchedAt: Instant? = null,
	var source: String? = null, // Source URL or identifier.
) {
	override fun toString(): String {
		return "ArticleMeta(title='$title', author=$author, date=$date, description=$description)"
	}
}

/**
 * Article is collected documents from picker scripts, which is the main content of the app.
 */
@Serializable
data class Article(
	var meta: ArticleMeta,
	var content: List<Div> = emptyList(),
) {
	override fun toString(): String {
		return "Article(meta=$meta, content=$content)"
	}

	fun toJSON(): String {
		return Json.encodeToString(this)
	}

	companion object {
		fun fromJSON(json: String): Article {
			return Json.decodeFromString(json)
		}
	}
}