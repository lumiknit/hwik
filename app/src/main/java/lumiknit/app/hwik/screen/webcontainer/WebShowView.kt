package lumiknit.app.hwik.screen.webcontainer

import android.util.Log
import android.view.KeyEvent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onKeyEvent
import lumiknit.app.hwik.components.TopBar
import lumiknit.app.hwik.ui.theme.LocalCustomColorsPalette

@Composable
fun WebShowView(
	modifier: Modifier = Modifier,
	url: String,
	onClose: (() -> Unit)? = null,
) {
	val url = remember { mutableStateOf("") }

	DisposableEffect(Unit) {
		Log.i("WebShowView", "WebShowView Mounted")

		var wvCallbacks = object : WebControlCallbacks() {
			override fun onURLChanged(newUrl: String) {
				url.value = newUrl
			}
		}
		WebController.addCallback(wvCallbacks)

		onDispose {
			Log.i("WebShowView", "WebShowView Disposed")
			WebController.removeCallback(wvCallbacks)
		}
	}

	Scaffold(
		topBar = {
			TopBar(
				title = "Web",
				onBack = {
					onClose?.invoke()
				}
			)
		},
		modifier = modifier
			.fillMaxSize(),
		containerColor = LocalCustomColorsPalette.current.background,
		contentColor = LocalCustomColorsPalette.current.onBackground,
	) { innerPadding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding)
		) {
			Row {
				Button(
					onClick = {
						WebController.go(
							WebTaskType.NAV_BACK
						)
					}
				) {
					Icon(
						modifier = Modifier,
						imageVector = Icons.AutoMirrored.Default.ArrowBack,
						contentDescription = "Back",
					)
				}
				Button(
					onClick = {}
				) {
					Icon(
						modifier = Modifier,
						imageVector = Icons.AutoMirrored.Default.ArrowForward,
						contentDescription = "Back",
					)
				}
				TextField(
					value = url.value,
					onValueChange = { newUrl ->
						url.value = newUrl
					},
					modifier = Modifier
						.weight(1f)
						.horizontalScroll(rememberScrollState())
						.onKeyEvent({ ev ->
							if (ev.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
								WebController.go(
									WebTaskType.NAV_TO,
									url.value
								)
								return@onKeyEvent true
							}
							false
						}),
					label = { Text("URL") },
					maxLines = 1,
					singleLine = true,
					keyboardActions = KeyboardActions(
						onDone = {
							WebController.go(
								WebTaskType.NAV_TO,
								url.value
							)
						}
					),
				)
			}
			ComposableWebView(
				modifier = Modifier.fillMaxSize(),
				url = "https://www.naver.com",
			)
		}
	}
}