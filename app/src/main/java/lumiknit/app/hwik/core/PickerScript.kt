package lumiknit.app.hwik.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Meta(
	var name: String? = null,
	var version: String? = null,
	var author: String? = null,
	var description: String? = null,
	var etc: MutableMap<String, String> = mutableMapOf(),
) {
	fun getTitle(): String {
		if (name != null) {
			if (version != null) {
				return "$name v$version"
			} else {
				return name!!
			}
		}
		return ""
	}
}

/**
 * PickerCond is a condition for a step in the extraction process.
 */
@Serializable
sealed class PickerCond

/**
 * CondPageReady represents the step should be executed when the page is ready.
 */
@Serializable
@SerialName("page_ready")
class CondPageReady : PickerCond()

/**
 * CondWait represents the step should be wait in seconds.
 */
@Serializable
@SerialName("wait")
class CondWait(val seconds: Double) : PickerCond()

/**
 * PickerStep is a step in the extration process.
 * Each step has a condition, when the script can be executed,
 * and a JavaScript code to be executed in WebView.
 */
@Serializable
data class PickerStep(
	var condWaitSeconds: Double = 0.0,
	var code: String = "",
)

@Serializable
data class PickerProcess(
	var steps: MutableList<PickerStep> = mutableListOf(),
) {
	fun toJSON(): String {
		return Json.encodeToString(this)
	}

	fun buildText(sb: StringBuilder, name: String) {
		sb.append("/// * $name\n\n")
		for (step in steps) {
			sb.append("/// -")
			if (step.condWaitSeconds > 0) {
				sb.append(" wait ${step.condWaitSeconds}")
			}
			sb.append("\n").append(step.code).append("\n\n")
		}
	}

	companion object {
		fun fromJSON(json: String): PickerProcess {
			return Json.decodeFromString(json)
		}
	}
}

private val prettyJSON = Json { prettyPrint = true }

/**
 * PickerScript is a scripts for web contents picker.
 * Based on this script, the app will find out useful information from web contents:
 * - List of articles
 * - Article content
 */
@Serializable
data class PickerScript(
	var id: String = "",
	var meta: Meta = Meta(),

	var urlRE: String = "",

	// Core scripts
	/** ArticleList: (lastState: JSONObject) => (JSONObject, URLs) */
	var articleList: PickerProcess = PickerProcess(),
	/** ArticleContent: (url: String) => Article */
	var articleContent: PickerProcess = PickerProcess(),
	/** Search: (query: String) => URLs */
	var search: PickerProcess = PickerProcess(),
) {
	fun toPrettyJSON(): String {
		return prettyJSON.encodeToString(this)
	}


	fun toJSON(): String {
		return Json.encodeToString(this)
	}

	fun toText(): String {
		val sb = StringBuilder()

		sb.append("///# $id\n")

		if (meta.name != null) {
			sb.append("///@name ${meta.name}\n")
		}
		if (meta.version != null) {
			sb.append("///@version ${meta.version}\n")
		}
		if (meta.author != null) {
			sb.append("///@author ${meta.author}\n")
		}
		if (meta.description != null) {
			sb.append("///@description ${meta.description}\n")
		}

		if (urlRE.isNotEmpty()) {
			sb.append("///@urlRE $urlRE\n")
		}

		sb.append("\n")

		if (articleList.steps.isNotEmpty()) {
			articleList.buildText(sb, "ArticleList")
		}

		if (articleContent.steps.isNotEmpty()) {
			articleContent.buildText(sb, "ArticleContent")
		}

		if (search.steps.isNotEmpty()) {
			search.buildText(sb, "Search")
		}

		return sb.toString()
	}

	companion object {
		fun fromJSON(json: String): PickerScript {
			return Json.decodeFromString(json)
		}

		fun fromText(text: String): PickerScript {
			val trimmed = text.trim()
			if (trimmed.startsWith("{")) {
				// If the text starts with '{', it may be JSON
				return fromJSON(trimmed)
			} else {
				// Otherwise, parse the text format
				return PickerScriptParser.parse(trimmed)
			}
		}
	}
}

