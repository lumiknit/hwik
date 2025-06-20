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

@Serializable
sealed class Condition

@Serializable
@SerialName("page_ready")
class CondPageReady : Condition()

@Serializable
@SerialName("wait")
class CondWait(val seconds: Double) : Condition()

@Serializable
data class Step(
	var condList: MutableList<Condition> = mutableListOf(),
	var code: String = "",
)

val prettyJSON = Json { prettyPrint = true }

@Serializable
data class SourceScript(
	var id: String = "",
	var meta: Meta = Meta(),
	var entryList: MutableList<Step> = mutableListOf(),
	var entry: MutableList<Step> = mutableListOf(),
) {
	fun toPrettyJSON(): String {
		return prettyJSON.encodeToString(this)
	}


	fun toJSON(): String {
		return Json.encodeToString(this)
	}

	companion object {
		fun fromJSON(json: String): SourceScript {
			return Json.decodeFromString(json)
		}

		fun fromJSONLines(jsonLines: String): List<SourceScript> {
			return jsonLines.lines().mapNotNull { line ->
				if (line.trim().isBlank()) null else fromJSON(line)
			}
		}
	}
}

