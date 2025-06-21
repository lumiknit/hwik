package lumiknit.app.hwik.screen.main

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
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
	dropdownModifier: Modifier = Modifier,
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
		Icon(Icons.Default.Search, contentDescription = "Add")
	}

	DropdownMenu(
		modifier = dropdownModifier,
		expanded = dropdownExpanded,
		onDismissRequest = { dropdownExpanded = false }
	) {
		DropdownMenuItem(
			text = { Text("Prev") },
			leadingIcon = {
				Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Prev")
			},
			onClick = {
				dropdownExpanded = false
				state.onPrevPage()
			}
		)
		DropdownMenuItem(
			text = { Text("Next") },
			leadingIcon = {
				Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Next")
			},
			onClick = {
				dropdownExpanded = false
				state.onNextPage()
			}
		)
		HorizontalDivider()
		DropdownMenuItem(
			text = { Text("Refresh") },
			leadingIcon = {
				Icon(Icons.Default.Refresh, contentDescription = "Refresh")
			},
			onClick = {
				dropdownExpanded = false
			}
		)
	}
}