package lumiknit.app.hwik.screen.webcontainer

import android.util.Log
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.serialization.json.JsonObject
import lumiknit.app.hwik.core.sanitizeFetchURL

// WebTaskType is a kind of task.

sealed class WebTaskType()

class WebTaskGetURL() : WebTaskType()
class WebTaskNavBack() : WebTaskType()
class WebTaskNavForward() : WebTaskType()
class WebTaskNavTo(val url: String) : WebTaskType()
class WebTaskRefresh() : WebTaskType()
class WebTaskEvalJS(val script: String, val state: JsonObject) : WebTaskType()

/**
 * WebTask is a data class for webview task queue item.
 */
data class WebTask(
	val type: WebTaskType,
	val callback: ((result: String, error: String?) -> Unit)? = null
)

/**
 * WebControlCallbacks is a base class for callbacks to handle web control events.
 */
open class WebControlCallbacks {
	open fun onURLChanged(url: String) {
		// This method can be overridden to handle URL changes
	}

	open fun onPageStarted() {
		// This method can be overridden to handle page load events
	}

	open fun onPageFinished(url: String) {
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

	var pageLoaded = false
		@Synchronized get
		@Synchronized set
}

/**
 * WebControlProvider is an object for the web feature provider.
 * For example, webview.
 */
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

	/**
	 * Callbacks for page started loading.
	 */
	fun onPageStarted() {
		WebControlCore.pageLoaded = false
		WebControlCore.callbacks.forEach { it.onPageStarted() }
	}

	/**
	 * Callbacks for page finished loading.
	 */
	fun onPageFinished(url: String) {
		WebControlCore.pageLoaded = true
		WebControlCore.callbacks.forEach { it.onPageFinished(url) }
	}
}

/**
 * TaskResult is a data class for the result of a web task.
 * It contains the result string and an optional error message.
 */
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

	fun addTask(
		taskType: WebTaskType,
		callback: ((result: String, error: String?) -> Unit)? = null
	) {
		val task = WebTask(type = taskType, callback = callback)
		WebControlCore.taskChannel.trySendBlocking(task)
	}

	suspend fun addTaskAsync(
		taskType: WebTaskType,
	): TaskResult {
		val waitChannel = Channel<TaskResult>(1)
		val task =
			WebTask(type = taskType, callback = { res, err ->
				waitChannel.trySendBlocking(TaskResult(result = res, error = err))
			})
		WebControlCore.taskChannel.send(task)
		return waitChannel.receive()
	}

	suspend fun runJS(
		script: String,
		state: JsonObject = JsonObject(emptyMap())
	): TaskResult {
		Log.d("WebCtrl:runJS", "Running script: $script")
		return addTaskAsync(
			WebTaskEvalJS(script, state)
		)
	}

	suspend fun reset() {
		goToAndWait("about:blank")
	}

	/**
	 * Go to a specific URL and wait for the page to load.
	 */
	suspend fun goToAndWait(
		url: String
	) {
		val url = sanitizeFetchURL(url)
		Log.d("WebCtrl:locationAndWait", "Navigating to $url")

		// Prepare the task to navigate to the URL
		val waitChannel = Channel<Unit>(1)
		val callbacks = object : WebControlCallbacks() {
			override fun onPageFinished(url: String) {
				Log.d("WebCtrl:locationAndWait", "Page loaded: $url")
				waitChannel.trySendBlocking(Unit)
			}
		}
		addCallback(callbacks)

		// Navigate to the URL
		val result = addTaskAsync(
			WebTaskNavTo(url),
		)
		if (result.error == null) {
			Log.d("WebCtrl:locationAndWait", "Waiting for page to load...")
			waitChannel.receive()
			Log.d("WebCtrl:locationAndWait", "Waiting for page to load done")
		}

		removeCallback(callbacks)

		if (result.error != null) {
			throw Exception("Error navigating to $url: ${result.error}")
		}
	}
}