package lumiknit.app.hwik

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import lumiknit.app.hwik.ui.theme.HwikTheme

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			HwikTheme {
				RootView()
			}
		}
	}

	override fun onDestroy() {
		super.onDestroy()
	}
}
