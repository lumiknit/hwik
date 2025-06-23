package lumiknit.app.hwik

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import lumiknit.app.hwik.state.ContentsVM
import lumiknit.app.hwik.state.GlobalVM
import lumiknit.app.hwik.ui.theme.HwikTheme

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()

		val context: Context = this

		setContent {
			LaunchedEffect(Unit) {
				GlobalVM
				ContentsVM.loadScriptsFromDB(context)
			}

			HwikTheme {
				RootView()
			}
		}
	}

	override fun onDestroy() {
		super.onDestroy()
	}
}
