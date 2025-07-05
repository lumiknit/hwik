package lumiknit.app.hwik.screen.webview

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking

// WebTaskType is a kind of task.

sealed class WebTaskType()

class WebTaskGetURL() : WebTaskType()
class WebTaskNavBack() : WebTaskType()
class WebTaskNavForward() : WebTaskType()
class WebTaskNavTo(val url: String) : WebTaskType()
class WebTaskRefresh() : WebTaskType()

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
}