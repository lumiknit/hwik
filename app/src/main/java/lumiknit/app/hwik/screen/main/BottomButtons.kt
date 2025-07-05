package lumiknit.app.hwik.screen.main

import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun BottomButtons(
	modifier: Modifier = Modifier,
	state: MainScreenState,
) {
	val context = LocalContext.current
	var searching by remember { mutableStateOf(false) }
	var searchText by remember { mutableStateOf("") }

	val btnModifier = Modifier
		.padding(0.dp, 4.dp)

	Row(
		modifier = modifier,
	) {
		if (searching) {
			TextButton(
				modifier = btnModifier,
				onClick = {
					searching = false
					searchText = ""
				},
				enabled = true,
			) {
				Icon(Icons.Default.Close, contentDescription = "Close")
			}

			TextField(
				modifier = btnModifier
					.weight(1f),
				value = searchText,
				onValueChange = {
					searchText = it
				},
				placeholder = { Text("Search") },
				singleLine = true,
				maxLines = 1,
			)
		} else {
			TextButton(
				modifier = btnModifier,
				onClick = {
					state.onPrevPage()
				},
				enabled = true,
			) {
				Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Prev")
			}

			TextButton(
				modifier = btnModifier,
				onClick = {
					state.onNextPage()
				},
				enabled = true,
			) {
				Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Next")
			}
			TextButton(
				modifier = btnModifier,
				onClick = {
				},
				enabled = true,
			) {
				Icon(Icons.AutoMirrored.Default.List, contentDescription = "List")
			}

			Spacer(modifier = Modifier.weight(1f))
		}

		TextButton(
			modifier = Modifier
				.padding(0.dp, 4.dp),
			onClick = {
				if (!searching) {
					searching = true
				} else if (searchText.isBlank()) {
					Toast.makeText(
						context,
						"Search text cannot be empty",
						Toast.LENGTH_SHORT
					).show()
				} else {
					state.onSearch(searchText)
					searching = false
				}
			},
			enabled = true,
		) {
			Icon(Icons.Default.Search, contentDescription = "Search")
		}
	}
}