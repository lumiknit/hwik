package lumiknit.app.hwik.state

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.suspendCancellableCoroutine
import lumiknit.app.hwik.components.modal.ConfirmModalProps
import lumiknit.app.hwik.components.modal.InputNumberModalProps
import lumiknit.app.hwik.components.modal.InputTextModalProps
import lumiknit.app.hwik.components.modal.ModalProps
import lumiknit.app.hwik.core.genUID

object ModalVM : ViewModel() {
	val modalList = mutableStateListOf<ModalProps>()

	fun openModal(modal: ModalProps) {
		val id = genUID()
		modal.id = id
		modal.onClosed = {
			modalList.removeIf { it.id == id }
		}
		modalList.add(modal)
	}

	suspend fun showConfirmModal(
		title: String,
		message: String,
	): Boolean {
		return suspendCancellableCoroutine { continuation ->
			var props: ConfirmModalProps? = null
			props = ConfirmModalProps(
				title = title,
				message = message,
				onClick = { confirmed ->
					props?.onClosed?.invoke()
					// Handle cancellation if needed
					continuation.resume(confirmed) { cause, _, _ -> }
				}
			)
			openModal(props)
		}
	}

	suspend fun showInputTextModal(
		title: String,
		message: String,
		initialValue: String = "",
	): String? {
		return suspendCancellableCoroutine { continuation ->
			var props: InputTextModalProps? = null
			props = InputTextModalProps(
				title = title,
				message = message,
				initialValue = initialValue,
				onClick = { input ->
					props?.onClosed?.invoke()
					// Handle cancellation if needed
					continuation.resume(input) { cause, _, _ -> }
				}
			)
			openModal(props)
		}
	}

	suspend fun showInputNumberModal(
		title: String,
		message: String,
		initialValue: Float = 0.0f,
	): Float? {
		return suspendCancellableCoroutine { continuation ->
			var props: InputNumberModalProps? =
				null
			props = InputNumberModalProps(
				title = title,
				message = message,
				initialValue = initialValue,
				onClick = { input ->
					props?.onClosed?.invoke()
					// Handle cancellation if needed
					continuation.resume(input) { cause, _, _ -> }
				}
			)
			openModal(props)
		}
	}
}