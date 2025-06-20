package lumiknit.app.hwik.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val mainPadding = PaddingValues(
	horizontal = 8.dp,
	vertical = 4.dp
)

val commonTextStyle = TextStyle(
	fontFamily = FontFamily.Default,
	fontWeight = FontWeight.Normal,
	fontSize = 16.sp,
	lineHeight = 20.sp,
	letterSpacing = 0.sp
)

val listSectionTitleTextStyle = TextStyle(
	fontFamily = FontFamily.Default,
	fontWeight = FontWeight.Bold,
	fontSize = 14.sp,
	lineHeight = 16.sp,
	letterSpacing = 0.sp
)

val listItemTitleTextStyle = TextStyle(
	fontFamily = FontFamily.Default,
	fontWeight = FontWeight.Bold,
	fontSize = 18.sp,
	lineHeight = 18.sp,
	letterSpacing = 0.sp
)

val listItemDescTextStyle = TextStyle(
	fontFamily = FontFamily.Default,
	fontWeight = FontWeight.Normal,
	fontSize = 13.sp,
	lineHeight = 14.sp,
	letterSpacing = 0.sp
)