package br.com.source.view.common


import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class StatusStyle {
    companion object {
        // message
        val cardTextColor = Color(red = 23, green = 43, blue = 77)
        val cardFontSize = 13.sp
        val cardFontWeight = FontWeight.ExtraLight
        val cardFontStyle = FontStyle.Normal

        // title
        val cardFontTitleWeight = FontWeight.SemiBold
        val cardFontTitleSize = 16.sp

        // empty message
        val cardFontEmptyWeight = FontWeight.Thin

        // background component
        val backgroundColor = Color(red = 247, green = 247, blue = 247)
    }
}



val cardBackgroundColor = Color(red = 236, green = 236, blue = 236)
val cardPadding = 4.dp
val cardTextPadding = 4.dp
const val cardRoundedCorner = 8f