package lumiknit.app.hwik.comp_webview

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import lumiknit.app.hwik.sourcescript.Step

enum class WebTaskType {
	GET_URL,
	NAV_BACK,
	NAV_FORWARD,
	NAV_TO,
	EVAL_JS,
}

data class WebTask(
	val type: WebTaskType,
	val data: String? = null,
	val callback: ((String) -> Unit)? = null
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
		callback: ((String) -> Unit)? = null
	) {
		val task = WebTask(type = taskType, data = data, callback = callback)
		WebControlCore.taskChannel.trySendBlocking(task)
	}

	suspend fun runScriptSteps(
		ss: List<Step>
	) {
	}
}
