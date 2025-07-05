package lumiknit.app.hwik

import androidx.compose.runtime.Composable
import lumiknit.app.hwik.state.ModalVM

@Composable
fun ModalContainer() {
	for (props in ModalVM.modalList) {
		props.render()
	}
}