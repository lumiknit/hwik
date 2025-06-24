package lumiknit.app.hwik.screen.sources

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import lumiknit.app.hwik.NavCallbacks
import lumiknit.app.hwik.components.TopBar
import lumiknit.app.hwik.components.list.ListSectionTitle
import lumiknit.app.hwik.core.PSDatabase
import lumiknit.app.hwik.core.PSSourceEntity
import lumiknit.app.hwik.core.PickerScript
import lumiknit.app.hwik.core.fetchPickerScript
import lumiknit.app.hwik.core.sanitizeFetchURL
import lumiknit.app.hwik.ui.theme.LocalCustomColorsPalette
import lumiknit.app.hwik.ui.theme.listItemDescTextStyle

@Composable
fun SourceEditScreen(
	navCallbacks: NavCallbacks,
	sourceID: Long? = null // Pass originId if editing an existing source, null for new
) {
	val context = LocalContext.current
	val coroutineScope = rememberCoroutineScope()
	val db =
		remember { PSDatabase.getDatabase(context) } // Remember the DB instance

	var url by remember { mutableStateOf("") }
	var rawScript by remember { mutableStateOf("") }
	var script by remember { mutableStateOf(PickerScript()) }

	var fetchingContents by remember { mutableStateOf(false) }
	var fetchedFromURL by remember { mutableStateOf(false) }

	var errorMsg by remember { mutableStateOf("") }

	LaunchedEffect(sourceID) {
		if (sourceID != null) {
			val origin = db.psScriptDao().getById(sourceID)
			if (origin == null) {
				errorMsg = "Source not found"
				Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
				navCallbacks.onBack() // Navigate back if source not found
				return@LaunchedEffect
			}
			url = origin.url ?: ""
			rawScript = origin.rawScript
			try {
				script = PickerScript.fromText(rawScript)
			} catch (e: Exception) {
				errorMsg = "Error parsing script: ${e.message}"
				Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
			}
		}
	}

	val handleLoadFromUrl = {
		coroutineScope.launch {
			if (url.isBlank()) {
				errorMsg = "URL cannot be empty"
				Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
				return@launch
			}

			fetchingContents = true
			val result = fetchPickerScript(url)
			fetchedFromURL = true
			fetchingContents = false

			if (result.raw != null) {
				rawScript = result.raw
			}
			if (result.script != null) {
				script = result.script
			}

			if (result.error != null) {
				errorMsg = "Error fetching script: ${result.error}"
				Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
			}
		}
		Unit
	}

	val tryToPack: (() -> PSSourceEntity?) = tryToPack@{
		val url = if (fetchedFromURL) sanitizeFetchURL(url) else null
		val rawScript = rawScript.trim()
		return@tryToPack try {
			val script = PickerScript.fromText(rawScript)
			val item = PSSourceEntity(
				id = sourceID ?: 0,
				url = url,
				rawScript = rawScript,
				script = script,
				lastFetched = Clock.System.now()
			)
			item
		} catch (e: Exception) {
			errorMsg = "Error parsing script: ${e.message}"
			Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
			null
		}
	}

	val handleSave = {
		coroutineScope.launch {
			var packed = tryToPack()
			if (packed != null) {
				if (sourceID != null) {
					// Update existing source
					db.psScriptDao().update(packed)
				} else {
					db.psScriptDao().insert(packed)
				}
				Toast.makeText(context, "Source saved successfully", Toast.LENGTH_SHORT)
					.show()
				navCallbacks.onBack() // Navigate back after saving
			} else {
				Toast.makeText(context, "Failed to save source", Toast.LENGTH_SHORT)
					.show()
			}
		}
		Unit
	}

	Scaffold(
		topBar = {
			TopBar(
				title = if (sourceID == null) "New Source" else "Edit Source",
				onBack = { navCallbacks.onBack() },
				onDone = handleSave,
			)
		},
		modifier = Modifier.fillMaxSize(),
		containerColor = LocalCustomColorsPalette.current.background,
		contentColor = LocalCustomColorsPalette.current.onBackground,
	) { innerPadding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding)
				.padding(16.dp) // Add some padding around the content
				.verticalScroll(rememberScrollState()) // Make content scrollable
		) {
			ListSectionTitle("Remote URL")
			Text(
				"If you have a remote source, you can enter its URL here. " +
						"Fetching the script will allow you to preview it before saving.",
				style = listItemDescTextStyle
			)
			OutlinedTextField(
				value = url,
				onValueChange = { url = it },
				placeholder = {
					Text("https://example.com/script.json")
				},
				label = { Text("URL") },
				modifier = Modifier.fillMaxWidth(),
				singleLine = true
			)
			Spacer(modifier = Modifier.height(8.dp))
			Button(
				onClick = handleLoadFromUrl,
				modifier = Modifier.align(Alignment.End),
				enabled = url.isNotBlank()
			) {
				if (fetchingContents) {
					CircularProgressIndicator(
						modifier = Modifier.size(24.dp), // Adjust size as needed
						strokeWidth = 2.dp, // Adjust stroke width as needed
						color = MaterialTheme.colorScheme.secondary,
						trackColor = MaterialTheme.colorScheme.surfaceVariant
					)
				} else {
					Text("Fetch Script")
				}
			}

			Spacer(modifier = Modifier.height(16.dp))

			ListSectionTitle("Script Code")
			TextField(
				value = rawScript,
				onValueChange = {
					rawScript = it
					fetchedFromURL = false
				},
				label = { Text("Picker Script") },
				modifier = Modifier
					.fillMaxWidth()
					.height(200.dp), // Adjust height as needed
				textStyle = TextStyle(
					fontFamily = FontFamily.Monospace
				),
				keyboardOptions =
					KeyboardOptions(keyboardType = KeyboardType.Ascii), // Good for code
				maxLines = 20
			)
			if (fetchedFromURL) {
				Text(
					text = "Script fetched from URL",
					color = MaterialTheme.colorScheme.primary,
					modifier = Modifier.padding(top = 8.dp)
				)
			}

			HorizontalDivider()

			ListSectionTitle("Script Edit")

			PSEdit(
				navCallbacks = navCallbacks,
				value = script,
				onValueChange = {
					script = it
					rawScript = it.toText()
				},
			)

			HorizontalDivider()

			if (errorMsg.isNotBlank()) {
				Text(
					text = errorMsg,
					color = MaterialTheme.colorScheme.error,
					modifier = Modifier.padding(top = 8.dp)
				)
			}

			Button(
				onClick = handleSave,
				modifier = Modifier.fillMaxWidth()
			) {
				Text("Done")
			}
			Spacer(modifier = Modifier.height(8.dp)) // Some spacing at the very bottom
		}
	}
}