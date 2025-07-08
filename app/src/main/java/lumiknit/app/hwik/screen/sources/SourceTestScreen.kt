package lumiknit.app.hwik.screen.sources

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import lumiknit.app.hwik.Navigator
import lumiknit.app.hwik.components.MenuItem
import lumiknit.app.hwik.components.TopBar
import lumiknit.app.hwik.components.list.ListSectionTitle
import lumiknit.app.hwik.core.PickerProcess
import lumiknit.app.hwik.screen.webworker.WebContext
import lumiknit.app.hwik.ui.theme.LocalCustomColorsPalette

val prettyJson = Json {
	prettyPrint = true
}

@Composable
fun SourceTestScreen(
	processJSON: String
) {
	val context = LocalContext.current
	val coroutineScope = rememberCoroutineScope()

	val scrollState = rememberScrollState()

	var process by remember { mutableStateOf<PickerProcess?>(null) }

	var inputState by remember { mutableStateOf("{\n}") }

	var running by remember { mutableStateOf(false) }
	var result by remember { mutableStateOf<WebContext.RunResult?>(null) }

	fun setError(message: String) {
		result = WebContext.RunResult(
			error = message
		)
	}

	suspend fun startTest() {
		result = null

		// Convert input to JSONObject
		var inputJSON: JsonObject
		try {
			val inputJson = inputState.ifBlank { null }
			if (inputJson == null) {
				setError("Input cannot be empty")
				return
			}
			inputJSON = Json.decodeFromString<JsonObject>(inputJson)
		} catch (e: Exception) {
			e.printStackTrace()
			setError("Invalid JSON input: ${e.message}")
			return
		}

		// Start with web controller
		try {
			running = true
			result = WebContext.runScript(
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
			Navigator.go(Navigator.RouteBack)
		}
	}

	Scaffold(
		topBar = {
			TopBar(
				title = "Sources",
				onBack = true,
				menuItems = listOf(
					MenuItem(title = "Source WebView", onClick = {
						Navigator.go(Navigator.RouteWebViewScreen)
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
				.verticalScroll(scrollState)
		) {
			TextField(
				value = inputState,
				onValueChange = { inputState = it },
				label = { Text("Input (JSON)") },
				modifier = Modifier.fillMaxWidth(),
				textStyle = TextStyle(
					fontFamily = FontFamily.Monospace
				)
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

			if (result?.stepResults != null) {
				val steps = result?.stepResults ?: emptyList()
				ListSectionTitle("Result")
				// Show index and monospace text for each result
				for ((index, res) in steps.withIndex()) {
					HorizontalDivider(
						modifier = Modifier.padding(vertical = 8.dp),
						color = MaterialTheme.colorScheme.onSurface,
						thickness = 1.dp
					)

					ListSectionTitle("Step ${index + 1} Code")

					// Show original code
					val code = """
// --- Code
${process?.steps[index]?.code}
					""".trimIndent()
					SelectionContainer {
						Text(
							text = code,
							style = TextStyle(
								fontFamily = FontFamily.Monospace,
							)
						)
					}

					ListSectionTitle("Step ${index + 1} Raw")

					Text(
						text = """
							${res.raw}
						""".trimIndent(),
						style = TextStyle(
							fontFamily = FontFamily.Monospace,
						)
					)

					ListSectionTitle("Step ${index + 1} Next State")

					SelectionContainer {
						Text(
							text = """
							${
								prettyJson.encodeToString(JsonObject.serializer(), res.state)
							}
						""".trimIndent(),
							style = TextStyle(
								fontFamily = FontFamily.Monospace,
							)
						)
					}
				}
			}
		}
	}
}