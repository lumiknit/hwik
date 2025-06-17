package lumiknit.app.hwik.core

import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

interface Div {}

@Serializable
data class TitleDiv(
	val text: String,
	val level: Int = 1,
) : Div {
	override fun toString(): String {
		return "Title(text='$text', level=$level)"
	}
}

@Serializable
data class ImageDiv(
	var url: String,
	var alt: String = "",
) : Div {
	override fun toString(): String {
		return "Image(url='$url', alt='$alt')"
	}
}

@Serializable
data class VideoDiv(
	var url: String,
	var alt: String = "",
) : Div {
	override fun toString(): String {
		return "Video(url='$url', alt='$alt')"
	}
}

@Serializable
data class ParagraphDiv(
	var content: List<Span>,
) : Div {
	override fun toString(): String {
		return "Paragraph(content=$content)"
	}
}

interface Span {}

@Serializable
data class SpanStyle(
	var bold: Boolean = false,
	var italic: Boolean = false,
	var underline: Boolean = false,
	var strikeThrough: Boolean = false,
	var lightRGB: String? = null, // Color for light mode.
) {
	override fun toString(): String {
		return "SpanStyle(bold=$bold, italic=$italic, underline=$underline, strikeThrough=$strikeThrough, lightRGB=$lightRGB)"
	}
}

@Serializable
data class TextSpan(
	var content: String,
	var style: SpanStyle = SpanStyle(),
) : Span {
	override fun toString(): String {
		return "TextSpan(text='$content', style=$style)"
	}
}

@Serializable
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
data class CodeSpan(
	var content: String,
	var language: String? = null, // Optional language for syntax highlighting.
	var style: SpanStyle = SpanStyle(),
) : Span {
	override fun toString(): String {
		return "CodeSpan(text='$content', language=$language, style=$style)"
	}
}

@Serializable
data class ArticleMeta(
	var title: String,
	var author: String? = null,
	@Contextual()
	var date: Instant? = null,
	var description: String? = null,
) {
	override fun toString(): String {
		return "ArticleMeta(title='$title', author=$author, date=$date, description=$description)"
	}
}

@Serializable
data class Article(
	var meta: ArticleMeta,
	var content: List<Div> = emptyList(),
) {
	override fun toString(): String {
		return "Article(meta=$meta, content=$content)"
	}
}