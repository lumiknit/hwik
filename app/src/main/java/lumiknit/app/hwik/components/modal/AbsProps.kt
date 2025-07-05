package lumiknit.app.hwik.components.modal

import androidx.compose.runtime.Composable

sealed class ModalProps(
	var id: String = "",
	var onClosed: (() -> Unit)? = null,
) {
	@Composable
	abstract fun render()
}