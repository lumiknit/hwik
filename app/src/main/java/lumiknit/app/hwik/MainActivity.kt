package lumiknit.app.hwik

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import lumiknit.app.hwik.compview.MainView
import lumiknit.app.hwik.ui.theme.HwikTheme
import lumiknit.app.hwik.ui.theme.LocalCustomColorsPalette

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			HwikTheme {
				Scaffold(
					topBar = {
						TopBar(title = "Main")
					},
					modifier = Modifier
						.fillMaxSize(),
					containerColor = LocalCustomColorsPalette.current.background,
					contentColor = LocalCustomColorsPalette.current.onBackground,
				) { innerPadding ->
					Box(modifier = Modifier.padding(innerPadding)) {
						MainView()
					}
				}
			}
		}
	}
}
