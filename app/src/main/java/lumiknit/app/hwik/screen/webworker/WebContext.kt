package lumiknit.app.hwik.screen.webworker

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.JsonObject
import lumiknit.app.hwik.core.PickerStep

data class TaskLog(
	val status: String,
	val kind: String,
	val message: String,
)


object WebContext : ViewModel() {
	var taskLogs = mutableStateListOf<TaskLog>()

	// Script Helpers
	data class StepResult(
		val index: Int,
		val raw: String,
		val state: JsonObject,
	)

	data class RunResult(
		val stepResults: List<StepResult> = listOf(),
		val finalResult: JsonObject = JsonObject(emptyMap()),
		val error: String? = null
	)

	data class Task(
		val steps: List<PickerStep>,
		val inputs: JsonObject,
		val onStepDone: (Int, StepResult) -> Unit = { _, _ -> },
		val onDone: (RunResult) -> Unit = { _ -> },
	)


	var workerCount by mutableIntStateOf(0)
		@Synchronized get
		@Synchronized set

	var taskChannel: Channel<Task> = Channel(1024)
		@Synchronized get
		@Synchronized set

	suspend fun runScript(
		steps: List<PickerStep>,
		inputs: JsonObject = JsonObject(emptyMap()),
		onStepDone: (Int, StepResult) -> Unit = { _, _ -> },
	): RunResult {
		val result = suspendCancellableCoroutine { continuation ->
			Log.i("WebContext", "runScript: creating webtask")
			val task = Task(
				steps = steps,
				inputs = inputs,
				onStepDone = onStepDone,
				onDone = { result ->
					continuation.resume(result) { cause, _, _ ->
						throw cause
					}
				}
			)
			Log.i("WebContext", "runScript: sent webtask")
			taskChannel.trySend(task)
		}
		Log.i("WebContext", "runScript: webtask done")

		return result
	}
}