package lumiknit.app.hwik

import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
	title: String,
	showBack: Boolean = false,
) {
	var moreExpanded by remember { mutableStateOf(false) }
	var context = LocalContext.current

	TopAppBar(
		colors = TopAppBarDefaults.topAppBarColors(),
		title = { Text(text = title) },
		navigationIcon = {
			if (!showBack) return@TopAppBar
			IconButton(onClick = {
				// Handle back navigation
				val activity = context as? androidx.activity.ComponentActivity
				activity?.onBackPressedDispatcher?.onBackPressed()
			}) {
				Icon(
					imageVector = Icons.AutoMirrored.Filled.ArrowBack,
					contentDescription = "Localized description"
				)
			}
		},
		actions = {
			IconButton(onClick = {
				moreExpanded = true
			}) {
				Icon(
					imageVector = Icons.Outlined.MoreVert,
					contentDescription = "Localized description"
				)
			}
			DropdownMenu(
				expanded = moreExpanded,
				onDismissRequest = { moreExpanded = false }
			) {
				DropdownMenuItem(
					text = { Text("Settings") },
					onClick = {
						Toast.makeText(context, "Settings", Toast.LENGTH_SHORT).show()
					}
				)
			}
		},
	)
}