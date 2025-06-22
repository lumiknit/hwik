package lumiknit.app.hwik.screen.sources

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import lumiknit.app.hwik.NavCallbacks
import lumiknit.app.hwik.components.MenuItem
import lumiknit.app.hwik.components.TopBar
import lumiknit.app.hwik.components.list.ListSectionTitle
import lumiknit.app.hwik.core.PickerProcess
import lumiknit.app.hwik.screen.webcontainer.WebController
import lumiknit.app.hwik.ui.theme.LocalCustomColorsPalette
import org.json.JSONObject

@Composable
fun SourceTestScreen(
	navCallbacks: NavCallbacks,
	processJSON: String
) {
	val context = LocalContext.current
	val coroutineScope = rememberCoroutineScope()

	var process by remember { mutableStateOf<PickerProcess?>(null) }

	var inputState by remember { mutableStateOf("{\n}") }

	var running by remember { mutableStateOf(false) }
	var result by remember { mutableStateOf<WebController.ScriptResults?>(null) }

	fun setError(message: String) {
		result = WebController.ScriptResults(
			error = message
		)
	}

	suspend fun startTest() {
		result = null

		// Convert input to JSONObject
		var inputJSON: JSONObject
		try {
			val inputJson = inputState.ifBlank { null }
			if (inputJson == null) {
				setError("Input cannot be empty")
				return
			}
			inputJSON = JSONObject(inputJson)
		} catch (e: Exception) {
			e.printStackTrace()
			setError("Invalid JSON input: ${e.message}")
			return
		}

		// Start with web controller
		try {
			running = true
			result = WebController.runScriptSteps(
				process!!.steps,
				inputJSON,
			)
		} catch (e: Exception) {
			e.printStackTrace()
			setError("Error during processing: ${e.message}")
			return
		} finally {
			running = false
		}
	}

	LaunchedEffect(processJSON) {
		try {
			process = PickerProcess.fromJSON(processJSON)
		} catch (e: Exception) {
			e.printStackTrace()
			navCallbacks.onBack()
		}
	}

	Scaffold(
		topBar = {
			TopBar(
				title = "Sources",
				onBack = {
					navCallbacks.onBack()
				},
				menuItems = listOf(
					MenuItem(title = "Source WebView", onClick = {
						navCallbacks.onRouteWebShowView()
					}),
				)
			)
		},
		modifier = Modifier
			.fillMaxSize(),
		containerColor = LocalCustomColorsPalette.current.background,
		contentColor = LocalCustomColorsPalette.current.onBackground,
	) { innerPadding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding)
				.padding(16.dp)
		) {
			TextField(
				value = inputState,
				onValueChange = { inputState = it },
				label = { Text("Input (JSON)") },
				modifier = Modifier.fillMaxWidth()
			)

			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 8.dp),
				horizontalArrangement = Arrangement.End
			) {
				Button(onClick = {
					coroutineScope.launch { startTest() }
				}) {
					Icon(Icons.Default.Build, contentDescription = "Test Process")
					Text("Test")
				}
			}

			if (running) {
				CircularProgressIndicator(
					modifier = Modifier.padding(vertical = 8.dp),
					color = MaterialTheme.colorScheme.secondary,
					trackColor = MaterialTheme.colorScheme.surfaceVariant,
				)
			}

			if (result?.error != null) {
				Text("Error: ${result?.error}", color = MaterialTheme.colorScheme.error)
			}

			if (result?.steps != null) {
				val steps = process?.steps ?: emptyList()
				Text("Results:")
				// Show index and monospace text for each result
				for ((index, result) in steps.withIndex()) {
					ListSectionTitle("Step ${index + 1}.")
					Text(
						text = "${index + 1}: $result",
						style = TextStyle(
							fontFamily = FontFamily.Monospace,
						)
					)
				}
			}
		}
	}
}