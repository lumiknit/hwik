package lumiknit.app.hwik.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Meta(
	var name: String? = null,
	var description: String? = null,
	var author: String? = null,
	var version: String? = null,
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

	companion object {
		fun fromJSON(json: String): PickerScript {
			return Json.decodeFromString(json)
		}
	}
}

