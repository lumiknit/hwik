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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lumiknit.app.hwik.NavCallbacks
import lumiknit.app.hwik.components.TopBar
import lumiknit.app.hwik.components.list.ListSectionTitle
import lumiknit.app.hwik.core.PSDatabase
import lumiknit.app.hwik.core.sanitizeFetchURL
import lumiknit.app.hwik.ui.theme.LocalCustomColorsPalette
import okhttp3.OkHttpClient
import okhttp3.Request

suspend fun fetchTextFromURL(url: String): String {
	val client = OkHttpClient()
	val request = Request.Builder()
		.url(sanitizeFetchURL(url))
		.build()

	return withContext(Dispatchers.IO) {
		try {
			client.newCall(request).execute().use { response ->
				if (!response.isSuccessful) throw Exception("Unexpected code $response")
				response.body?.string() ?: ""
			}
		} catch (e: Exception) {
			"Error fetching script: ${e.message}"
		}
	}
}

@Composable
fun SourceEditScreen(
	navCallbacks: NavCallbacks,
	sourceID: String? = null // Pass originId if editing an existing source, null for new
) {
	val context = LocalContext.current
	val coroutineScope = rememberCoroutineScope()
	val db =
		remember { PSDatabase.getDatabase(context) } // Remember the DB instance

	var url by remember { mutableStateOf("") }
	var rawScript by remember { mutableStateOf("") }

	var fetchingContents by remember { mutableStateOf(false) }
	var fetchedFromURL by remember { mutableStateOf(false) }

	// TODO: Load existing origin and script if originId is not null
	//  LaunchedEffect(originId) {
	//      if (originId != null) {
	//          val origin = db.ssOriginDao().getById(originId)
	//          val scriptItem = db.ssScriptDao().getByOriginId(originId) // You'll need to add this DAO method
	//          origin?.let { url = it.url }
	//          scriptItem?.let { directScript = it.script /* or however you store it */ }
	//      }
	//  }

	val handleLoadFromUrl = {
		coroutineScope.launch {
			if (url.isNotBlank()) {
				fetchingContents = true
				rawScript = fetchTextFromURL(url)
				fetchedFromURL = true
				fetchingContents = false
			} else {
				Toast.makeText(context, "URL cannot be empty", Toast.LENGTH_SHORT)
					.show()
			}
		}
		Unit
	}

	val handleSave = {
		coroutineScope.launch {
			// TODO: Implement your save logic
			// This will involve creating or updating SSOriginEntity and SSItemEntity
			// and then saving them to the database.
			// Consider if the script comes from the URL or direct input.

			// Example (very basic, needs more robust logic):
			// if (originId == null) { // New source
			//     val newOrigin = SSOriginEntity(url = url, lastFetched = Clock.System.now())
			//     val newOriginId = db.ssOriginDao().insert(newOrigin) // Assuming insert returns the ID
			//     val newScript = SSItemEntity(
			//         originID = newOriginId,
			//         script = if (fetchedScript.isNotBlank()) fetchedScript else directScript,
			//         // ... other fields
			//     )
			//     db.ssScriptDao().insert(newScript)
			// } else { // Existing source
			//     // Update logic
			// }
			navCallbacks.onBack() // Go back after saving
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
						"Fetching the script will allow you to preview it before saving."
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
				label = { Text("Picker Script in JSON") },
				modifier = Modifier
					.fillMaxWidth()
					.height(200.dp), // Adjust height as needed
				keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii), // Good for code
				maxLines = 20
			)
			if (fetchedFromURL) {
				Text(
					text = "Script fetched from URL",
					color = MaterialTheme.colorScheme.primary,
					modifier = Modifier.padding(top = 8.dp)
				)
			}

			Spacer(modifier = Modifier.weight(1f)) // Push button to the bottom

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