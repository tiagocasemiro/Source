package br.com.source.view.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object StatusStyle {
    // message
    val cardTextColor = Color(red = 23, green = 43, blue = 77)
    val cardFontSize = 13.sp
    val cardFontWeight = FontWeight.Normal
    val cardFontStyle = FontStyle.Normal

    // title
    val cardFontTitleWeight = FontWeight.Bold
    val cardFontTitleSize = 16.sp

    // empty message
    val cardFontEmptyWeight = FontWeight.Light

    // background component
    val backgroundColor = Color(red = 247, green = 247, blue = 247)
    val primaryButtonColor = Color(0,34,234)
    val negativeButtonColor = Color(196,196,196)
    val textFieldColor = Color(108,121,142)
    val titleAlertColor = Color(23,43,77)
}

object ErrorColor {
    val color: Color = Color(185,0,0)
}
object WarnColor {
    val color: Color = Color(208,126,1)
}
object InfoColor {
    val color: Color = Color(red = 23, green = 43, blue = 77)
}
object DefaultColor {
    val color: Color = Color(red = 23, green = 43, blue = 77)
}
object SuccessColor {
    val color = Color(36,146,47)
}

val cardBackgroundColor = Color(red = 236, green = 236, blue = 236)
val dialogBackgroundColor = Color(red = 241, green = 241, blue = 241)
val itemRepositoryBackground = Color(225,225,225)
val itemRepositoryHoveBackground = Color(23,43,77,50)
val itemBranchHoveBackground = Color(25,25,30,10)
val itemRepositoryText = Color(23,43,77,255)
val hoverDeleteRepository = Color(23,43,77,100)
val cardPadding = 4.dp
val appPadding = 10.dp
val cardTextPadding = 4.dp
const val cardRoundedCorner = 8f