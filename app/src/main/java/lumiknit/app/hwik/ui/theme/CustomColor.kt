package lumiknit.app.hwik.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class CustomColorsPalette(
	val appBarBackground: Color = Color.Unspecified,
	val onAppBarBackground: Color = Color.Unspecified,

	val background: Color = Color.Unspecified,
	val onBackground: Color = Color.Unspecified,
	val linkText: Color = Color.Unspecified,
)

val OnLightCustomColorsPalette = CustomColorsPalette(
	appBarBackground = MainColor,
	onAppBarBackground = Color(color = 0xFF121213),
	background = Color(0xFFFFFFFF),
	onBackground = Color(0xFF121213),
	linkText = Color(0xFF2398EC),
)

val OnDarkCustomColorsPalette = CustomColorsPalette(
	appBarBackground = Color(0xFF000000),
	onAppBarBackground = Color(color = 0xFFFFFFFF),
	background = Color(0xFF000000),
	onBackground = Color(0xFFF8F8F8),
	linkText = Color(0xFFFFAC4D),
)

val LocalCustomColorsPalette =
	staticCompositionLocalOf { CustomColorsPalette() }