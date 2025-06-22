package lumiknit.app.hwik.screen.webcontainer

import android.util.Log
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import lumiknit.app.hwik.core.PickerStep
import org.json.JSONObject

enum class WebTaskType {
	GET_URL,
	NAV_BACK,
	NAV_FORWARD,
	NAV_TO,
	REFRESH,
	EVAL_JS,
}

data class WebTask(
	val type: WebTaskType,
	val data: String? = null,
	val callback: ((result: String, error: String?) -> Unit)? = null
)

open class WebControlCallbacks {
	open fun onURLChanged(url: String) {
		// This method can be overridden to handle URL changes
	}

	open fun onPageLoaded(url: String) {
		// This method can be overridden to handle page load events
	}
}

/**
 * WebController is an object to manage web-view and javascript interactions.
 * This is used for handling web content, JavaScript execution, and communication
 */
private object WebControlCore {
	var isConnected: Boolean = false
		@Synchronized get
		@Synchronized set
	var taskChannel: Channel<WebTask> = Channel(128)
		@Synchronized get
		@Synchronized set
	var callbacks: MutableList<WebControlCallbacks> = mutableListOf()
		@Synchronized get
		@Synchronized set
}

internal object WebControlProvider {
	fun connect() {
		if (WebControlCore.isConnected) return
		WebControlCore.isConnected = true
	}

	suspend fun popTask(): WebTask {
		return WebControlCore.taskChannel.receive()
	}

	fun onURLChanged(url: String?) {
		if (url == null) return
		WebControlCore.callbacks.forEach { it.onURLChanged(url) }
	}
}

data class TaskResult(
	val result: String,
	val error: String? = null
)

/**
 * WebController is an interface for who wants to control web-view and javascript interactions.
 */
object WebController {
	fun addCallback(callback: WebControlCallbacks) {
		WebControlCore.callbacks.add(callback)
	}

	fun removeCallback(callback: WebControlCallbacks) {
		WebControlCore.callbacks.remove(callback)
	}

	fun go(
		taskType: WebTaskType,
		data: String? = null,
		callback: ((result: String, error: String?) -> Unit)? = null
	) {
		val task = WebTask(type = taskType, data = data, callback = callback)
		WebControlCore.taskChannel.trySendBlocking(task)
	}

	suspend fun goAsync(
		taskType: WebTaskType,
		data: String? = null
	): TaskResult {
		val waitChannel = Channel<TaskResult>(1)
		val task = WebTask(type = taskType, data = data, callback = { res, err ->
			waitChannel.trySendBlocking(TaskResult(result = res, error = err))
		})
		WebControlCore.taskChannel.send(task)
		return waitChannel.receive()
	}

	data class StepResult(
		val raw: String,
		val state: JSONObject,
	)

	data class ScriptResults(
		val steps: List<StepResult> = listOf(),
		val finalResult: JSONObject = JSONObject(),
		val error: String? = null
	)

	suspend fun runScriptSteps(
		ss: List<PickerStep>,
		inputs: JSONObject = JSONObject()
	): ScriptResults {
		val results = mutableListOf<StepResult>()
		var error: String? = null

		Log.d("WebCtrl:runScriptSteps", "runScriptSteps: $ss, inputs: $inputs")

		Log.d("WebCtrl:runScriptSteps", "goto about:blank to reset state")

		// Before starting, go to about:blank to reset the webview
		val res = goAsync(WebTaskType.NAV_TO, "about:blank")
		if (res.error != null) {
			return ScriptResults(
				results,
				error = "Error to reset state: ${res.error}"
			)
		}

		Log.d("WebCtrl:runScriptSteps", "reset done, starting steps")

		var state = inputs

		for ((idx, step) in ss.withIndex()) {
			delay(5000)

			Log.d("WebCtrl:runScriptSteps", "Step $idx: ${step.code}")

			val script =
				"""
					JSON.stringify((function($) {
					  ${step.code}
					})($state))
				""".trimIndent()
			try {
				val result = goAsync(WebTaskType.EVAL_JS, script)
				if (result.error != null) {
					error =
						"Error executing step $idx, RunError: ${result.error}"
					break
				}
				try {
					val newObject = JSONObject(result.result)
					val newState = JSONObject()
					for (key in state.keys()) {
						newState.put(key, state.get(key))
					}
					for (key in newObject.keys()) {
						state.put(key, newObject.get(key))
					}
					state = newState
				} catch (e: Exception) {
					Log.e(
						"WebCtrl:runScriptSteps",
						"Error parsing JSON result from step $idx: ${e.message}"
					)
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

		return ScriptResults(
			results, state, error
		)
	}
}
