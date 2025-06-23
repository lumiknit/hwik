package lumiknit.app.hwik.state

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.suspendCancellableCoroutine
import lumiknit.app.hwik.components.modal.ConfirmModalProps

object GlobalVM : ViewModel() {
	// Modal
	val confirmModalCallback = mutableStateOf<ConfirmModalProps?>(null)

	suspend fun showConfirmModal(
		title: String,
		message: String,
	): Boolean {
		return suspendCancellableCoroutine { continuation ->
			confirmModalCallback.value = ConfirmModalProps(
				title = title,
				message = message,
				onClick = { confirmed ->
					// Handle cancellation if needed
					continuation.resume(confirmed) { cause, _, _ -> // Handle cancellation if needed
						// Handle cancellation if needed
					}
					confirmModalCallback.value = null // Clear the callback after use
				}
			)
		}
	}
}