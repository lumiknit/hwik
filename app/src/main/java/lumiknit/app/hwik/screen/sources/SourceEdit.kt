package lumiknit.app.hwik.screen.sources

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import lumiknit.app.hwik.NavCallbacks
import lumiknit.app.hwik.components.list.ListSectionTitle
import lumiknit.app.hwik.core.PickerProcess
import lumiknit.app.hwik.core.PickerScript
import lumiknit.app.hwik.core.PickerStep

fun String.nullIfBlank(): String? {
	return if (this.isBlank()) null else this
}

@Composable
fun PSEdit(
	navCallbacks: NavCallbacks,
	value: PickerScript,
	onValueChange: (PickerScript) -> Unit,
) {

	val textFieldModifier = Modifier
		.fillMaxWidth()
		.padding(bottom = 8.dp)

	HorizontalDivider()

	ListSectionTitle("Meta")

	// ID
	TextField(
		value = (value.id),
		onValueChange = {
			onValueChange(
				value.copy(
					id = it
				)
			)
		},
		singleLine = true,
		modifier = textFieldModifier,
		label = { Text("ID") }
	)

	// Meta.name
	TextField(
		value = (value.meta.name ?: ""),
		onValueChange = {
			onValueChange(
				value.copy(
					meta = value.meta.copy(name = it.nullIfBlank())
				)
			)
		},
		singleLine = true,
		modifier = textFieldModifier,
		label = { Text("Name") }
	)

	// Meta.version
	TextField(
		value = (value.meta.version ?: ""),
		onValueChange = {
			onValueChange(
				value.copy(
					meta = value.meta.copy(version = it.nullIfBlank())
				)
			)
		},
		singleLine = true,
		modifier = textFieldModifier,
		label = { Text("Version") }
	)

	// Meta.author
	TextField(
		value = (value.meta.author ?: ""),
		onValueChange = {
			onValueChange(
				value.copy(
					meta = value.meta.copy(author = it.nullIfBlank())
				)
			)
		},
		singleLine = true,
		modifier = textFieldModifier,
		label = { Text("Author") }
	)

	// Meta.description
	TextField(
		value = (value.meta.description ?: ""),
		onValueChange = {
			onValueChange(
				value.copy(
					meta = value.meta.copy(description = it.nullIfBlank())
				)
			)
		},
		modifier = textFieldModifier,
		label = { Text("Description") }
	)

	TextField(
		value = value.urlRE,
		onValueChange = { onValueChange(value.copy(urlRE = it)) },
		singleLine = true,
		modifier = textFieldModifier,
		label = { Text("URL Regexp") }
	)

	EditProcess(
		navCallbacks = navCallbacks,
		name = "Article List",
		process = value.articleList,
		onValueChange = { onValueChange(value.copy(articleList = it)) }
	)

	EditProcess(
		navCallbacks = navCallbacks,
		name = "Article Content",
		process = value.articleContent,
		onValueChange = { onValueChange(value.copy(articleContent = it)) }
	)

	EditProcess(
		navCallbacks = navCallbacks,
		name = "Search",
		process = value.search,
		onValueChange = { onValueChange(value.copy(search = it)) }
	)
}

@Composable
private fun EditProcess(
	navCallbacks: NavCallbacks,
	name: String,
	process: PickerProcess,
	onValueChange: (PickerProcess) -> Unit,
) {
	HorizontalDivider()
	ListSectionTitle("Script: $name")

	Column(
	) {
		Button(onClick = {
			navCallbacks.onRouteSourceTest(process.toJSON())
		}) {
			Text("Test")
		}

		process.steps.forEachIndexed { index, step ->
			EditProcessStep(
				index = index,
				step = step,
				onStepChange = { newStep ->
					val newSteps = process.steps.toMutableList()
					newSteps[index] = newStep
					onValueChange(process.copy(steps = newSteps))
				},
				onMoveUp = {
					if (index > 0) {
						val newSteps = process.steps.toMutableList()
						val temp = newSteps[index]
						newSteps[index] = newSteps[index - 1]
						newSteps[index - 1] = temp
						onValueChange(process.copy(steps = newSteps))
					}
				},
				onMoveDown = {
					if (index < process.steps.size - 1) {
						val newSteps = process.steps.toMutableList()
						val temp = newSteps[index]
						newSteps[index] = newSteps[index + 1]
						newSteps[index + 1] = temp
						onValueChange(process.copy(steps = newSteps))
					}
				},
				onDelete = {
					val newSteps = process.steps.toMutableList()
					newSteps.removeAt(index)
					onValueChange(process.copy(steps = newSteps))
				}
			)
			Spacer(modifier = Modifier.height(8.dp))
		}
	}

	// Add button
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.Center
	) {
		TextButton(onClick = {
			val newSteps = process.steps.toMutableList()
			newSteps.add(PickerStep())
			onValueChange(process.copy(steps = newSteps))
		}) {
			Icon(Icons.Filled.Add, contentDescription = "Add Step")
			Text("Add Step")
		}
	}
}

@Composable
private fun EditProcessStep(
	index: Int,
	step: PickerStep,
	onStepChange: (PickerStep) -> Unit,
	onMoveUp: () -> Unit,
	onMoveDown: () -> Unit,
	onDelete: () -> Unit,
) {
	Box(
		modifier = Modifier.border(
			width = 1.dp,
			color = MaterialTheme.colorScheme.outline,
			shape = MaterialTheme.shapes.small
		)
	) {
		Column(
			modifier = Modifier
				.padding(8.dp),
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Text("Step ${index + 1}")
				Row {
					IconButton(onClick = onMoveUp) {
						Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Move Up")
					}
					IconButton(onClick = onMoveDown) {
						Icon(
							Icons.Filled.KeyboardArrowDown,
							contentDescription = "Move Down"
						)
					}
					IconButton(onClick = onDelete) {
						Icon(Icons.Filled.Delete, contentDescription = "Delete")
					}
				}
			}

			TextField(
				value = step.code,
				onValueChange = { onStepChange(step.copy(code = it)) },
				label = { Text("JavaScript Code") },
				modifier = Modifier.fillMaxWidth(),
				textStyle = TextStyle(
					fontFamily = FontFamily.Monospace,
				),
				minLines = 3
			)
		}
	}
}