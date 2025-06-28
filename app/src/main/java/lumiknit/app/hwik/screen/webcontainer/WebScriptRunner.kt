package lumiknit.app.hwik.screen.webcontainer

import android.util.Log
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import lumiknit.app.hwik.core.PickerStep

object WebScriptRunner {
	data class StepResult(
		val raw: String,
		val state: JsonObject,
	)

	data class RunResult(
		val stepResults: List<StepResult> = listOf(),
		val finalResult: JsonObject = JsonObject(emptyMap()),
		val error: String? = null
	)

	fun filterTemporarayFields(
		state: JsonObject
	) = JsonObject(state.filterKeys { !it.startsWith("$") })

	/**
	 * runScriptSteps executes a list of PickerStep scripts in sequence.
	 * Each step can modify the state, which is passed to the next step.
	 * The final result is returned as a ScriptResults object.
	 */
	suspend fun runScriptSteps(
		ss: List<PickerStep>,
		inputs: JsonObject = JsonObject(emptyMap())
	): RunResult {
		val results = mutableListOf<StepResult>()
		var error: String? = null

		Log.d("WebCtrl:runScriptSteps", "runScriptSteps: $ss, inputs: $inputs")

		// Before starting, go to about:blank to reset the webview
		WebController.reset()

		var state = inputs

		Log.d("WebCtrl:runScriptSteps", "Initial state: $state")

		for ((idx, step) in ss.withIndex()) {
			Log.d("WebCtrl:runScriptSteps", "Step $idx: ${step.code}")

			if (idx > 0) {
				state = filterTemporarayFields(state)
				Log.d(
					"WebCtrl:runScriptSteps",
					"State filtered (remove temp fields): $state"
				)
			}

			val script = wrapPickerScript(step.code)
			try {
				val result = WebController.runJS(script, state)
				if (result.error != null) {
					error =
						"Error executing step $idx, RunError: ${result.error}"
					break
				}
				Log.d("WebCtrl:runScriptSteps", "Step $idx result: ${result.result}")

				state = buildJsonObject {
					state.forEach { (key, value) ->
						put(key, value)
					}
					try {
						val newObject = Json.decodeFromString<JsonObject>(result.result)
						newObject.forEach { (key, value) ->
							put(key, value)
						}
					} catch (e: Exception) {
						Log.e(
							"WebCtrl:runScriptSteps",
							"Error parsing JSON result from step $idx: ${e.message}"
						)
					}
				}

				// If the state has '$href' field, goToAndWait
				if (state.containsKey("\$href")) {
					val field = state["\$href"]!!
					if (!field.jsonPrimitive.isString) {
						error = "Error executing step $idx, \$href is not a string"
						break
					}
					val href = field.jsonPrimitive.content
					Log.d("WebCtrl:runScriptSteps", "Step $idx href: $href")
					WebController.goToAndWait(href)
					Log.d("WebCtrl:runScriptSteps", "Step $idx href done")
				}

				results.add(
					StepResult(
						raw = result.result,
						state = state
					)
				)
			} catch (e: Exception) {
				error = "Error executing step $idx, Error: ${e.message}"
				break
			}
		}
		Log.d(
			"WebCtrl:runScriptSteps",
			"Finished running steps, outputs: $results, error: $error"
		)

		// Go to about:blank to reset the webview again
		WebController.reset()

		return RunResult(
			results, state, error
		)
	}
}