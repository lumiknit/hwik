package lumiknit.app.hwik.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import lumiknit.app.hwik.Navigator
import lumiknit.app.hwik.ui.theme.LocalCustomColorsPalette

data class MenuItem(
	val title: String,
	val icon: (@Composable () -> Unit)? = null,
	val onClick: () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
	title: String,
	menuItems: List<MenuItem>? = null,
	onBack: Boolean = false,
	onDone: (() -> Unit)? = null,
) {
	var moreExpanded by remember { mutableStateOf(false) }
	val context = LocalContext.current

	val colors = TopAppBarColors(
		containerColor = LocalCustomColorsPalette.current.appBarBackground,
		scrolledContainerColor = LocalCustomColorsPalette.current.appBarBackground,
		navigationIconContentColor = LocalCustomColorsPalette.current.onAppBarBackground,
		titleContentColor = LocalCustomColorsPalette.current.onAppBarBackground,
		actionIconContentColor = LocalCustomColorsPalette.current.onAppBarBackground,
	)

	TopAppBar(
		colors = colors,
		title = {
			Text(
				text = title,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis
			)
		},
		navigationIcon = {
			if (onBack) {
				IconButton(onClick = {
					Navigator.go(Navigator.RouteBack)
				}) {
					Icon(
						imageVector = Icons.AutoMirrored.Filled.ArrowBack,
						contentDescription = "Localized description"
					)
				}
			}
		},
		actions = {
			if (menuItems != null) {
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
					for (item in menuItems) {
						DropdownMenuItem(
							text = { Text(item.title) },
							leadingIcon = item.icon,
							onClick = {
								item.onClick()
								moreExpanded = false
							}
						)
					}
				}
			}
			if (onDone != null) {
				IconButton(onClick = onDone) {
					Icon(
						imageVector = Icons.Default.Check,
						contentDescription = "Done"
					)
				}
			}
		},
	)
}