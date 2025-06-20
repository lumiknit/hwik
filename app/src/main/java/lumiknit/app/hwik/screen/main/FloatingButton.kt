package lumiknit.app.hwik.screen.main

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FloatingButton(
	modifier: Modifier = Modifier,
	state: MainScreenState,
) {
	var dropdownExpanded by remember { mutableStateOf(false) }

	// Center bottom
	FloatingActionButton(
		modifier = modifier
			.size(52.dp)
			.padding(4.dp),
		shape = CircleShape,
		onClick = {
			dropdownExpanded = !dropdownExpanded
		}
	) {
		Icon(Icons.Filled.MoreVert, contentDescription = "Add")
	}

	DropdownMenu(
		modifier = Modifier,
		expanded = dropdownExpanded,
		onDismissRequest = { dropdownExpanded = false }
	) {
		DropdownMenuItem(
			text = { Text("Prev") },
			onClick = {
				dropdownExpanded = false
				state.onPrevPage()
			}
		)
		DropdownMenuItem(
			text = { Text("Next") },
			onClick = {
				dropdownExpanded = false
				state.onNextPage()
			}
		)
		DropdownMenuItem(
			text = { Text("Refresh") },
			onClick = {
				dropdownExpanded = false
			}
		)
	}
}