package lumiknit.app.hwik

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import lumiknit.app.hwik.state.ContentsVM
import lumiknit.app.hwik.state.GlobalVM
import lumiknit.app.hwik.ui.theme.HwikTheme

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()

		val context: Context = this

		setContent {
			val coroutineScope = rememberCoroutineScope()

			LaunchedEffect(Unit) {
				GlobalVM
				GlobalVM.setUpImageLoader(context)
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
