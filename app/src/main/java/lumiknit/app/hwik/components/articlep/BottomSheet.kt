package lumiknit.app.hwik.components.articlep

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import lumiknit.app.hwik.NavCallbacks
import lumiknit.app.hwik.R
import lumiknit.app.hwik.components.MenuItem
import lumiknit.app.hwik.components.transparentTextFieldColors
import lumiknit.app.hwik.state.ContentsVM
import lumiknit.app.hwik.ui.theme.LocalCustomColorsPalette

@Composable
private fun Buttons(
	state: ArticlePageState,
	menuItems: List<MenuItem>,
) {
	val btnModifier = Modifier
		.padding(0.dp, 4.dp)

	var moreExpanded by remember { mutableStateOf(false) }

	Row {
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

		TextButton(
			modifier = btnModifier,
			onClick = {
				moreExpanded = true
			},
			enabled = true,
		) {
			Icon(Icons.Default.MoreVert, contentDescription = "More")
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
	}
}

@Composable
private fun SearchView(
	navCallbacks: NavCallbacks
) {
	val keyboardController = LocalSoftwareKeyboardController.current
	var searchText by remember { mutableStateOf("") }

	val btnModifier = Modifier
		.padding(0.dp, 0.dp)

	Row(
		modifier = Modifier
			.padding(8.dp)
			.fillMaxWidth()
			.clip(shape = RoundedCornerShape(8.dp))
			.background(color = LocalCustomColorsPalette.current.inputBackground),
		verticalAlignment = Alignment.CenterVertically
	) {
		if (!searchText.isEmpty()) {
			IconButton(
				modifier = btnModifier,
				onClick = {
					searchText = ""
				},
				enabled = true,
			) {
				Icon(Icons.Default.Close, contentDescription = "Close")
			}
		}

		TextField(
			modifier = btnModifier
				.weight(1f),
			value = searchText,
			colors = transparentTextFieldColors(),
			onValueChange = {
				searchText = it
			},
			placeholder = { Text("Search") },
			singleLine = true,
			maxLines = 1,
			keyboardOptions = KeyboardOptions(
				imeAction = ImeAction.Search
			),
			keyboardActions = KeyboardActions(
				onSearch = {
					keyboardController?.hide()
					navCallbacks.onRouteSearched(searchText)
				}
			)
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
	modifier: Modifier = Modifier,
	navCallbacks: NavCallbacks,
	state: ArticlePageState,
) {

	var expandSheet by remember { mutableStateOf(false) }

	val menuItems = listOf(
		MenuItem(
			title = "${stringResource(R.string.dd_menu_sources)}(${ContentsVM.pickers.size})",
			onClick = {
				navCallbacks.onRouteSourceList()
			}),
		MenuItem(title = stringResource(R.string.dd_menu_webview), onClick = {
			navCallbacks.onRouteWebShowView()
		}),
		MenuItem(
			title = stringResource(R.string.dd_menu_preferences),
			onClick = {
				navCallbacks.onRoutePreferences()
			}),
	)


	// Fab to toggle the bottom sheet
	if (expandSheet) {
		ModalBottomSheet(
			onDismissRequest = {
				expandSheet = false
			},
			modifier =
				Modifier.pointerInput(Unit) {
					detectVerticalDragGestures { _, dragAmount ->
						if (dragAmount > 0) {
							// Dragging down, expand the sheet
							expandSheet = true
						} else if (dragAmount < 0) {
							// Dragging up, collapse the sheet
							expandSheet = false
						}
					}
				}
		) {
			Column {
				Buttons(state = state, menuItems)
				SearchView(navCallbacks)
			}
		}
	}

	AnimatedVisibility(
		!expandSheet,
		modifier = modifier,
		enter = slideInVertically(),
		exit = slideOutVertically()
	) {
		FloatingActionButton(
			shape = RoundedCornerShape(50),
			onClick = {
				expandSheet = true
			},
		) {
			Icon(
				imageVector = Icons.Default.Menu,
				contentDescription = "Expand Bottom Sheet",
			)
		}
	}
}