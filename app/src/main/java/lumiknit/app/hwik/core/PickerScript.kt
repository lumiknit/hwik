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
)

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
	var condList: MutableList<PickerCond> = mutableListOf(),
	var code: String = "",
)

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

	// Core scripts
	var articleList: MutableList<PickerStep> = mutableListOf(),
	var articleContent: MutableList<PickerStep> = mutableListOf(),
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

		fun fromJSONLines(jsonLines: String): List<PickerScript> {
			return jsonLines.lines().mapNotNull { line ->
				if (line.trim().isBlank()) null else fromJSON(line)
			}
		}
	}
}

