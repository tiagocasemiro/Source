package br.com.source.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.source.view.common.Fonts
import br.com.source.view.common.StatusStyle.Companion.textFieldColor

@Composable
fun SourceTextField(
    text: String,
    label: String = "",
    placeholder: String = "",
    fontSize: TextUnit = 13.sp,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    val textRemember = remember { mutableStateOf(text) }
    val modifier = Modifier
    Column {
        if(label.isNotEmpty()) {
            Text(
                label,
                style = LocalTextStyle.current.copy(
                    color = textFieldColor.copy(alpha = 0.8f),
                    fontSize = (fontSize.value - 3f).sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Fonts.roboto()
                )
            )
        }
        BasicTextField(
            value = textRemember.value,
            modifier = modifier
                .background(
                    MaterialTheme.colors.surface,
                    MaterialTheme.shapes.small,
                )
                .border(width = 1.dp, color = textFieldColor, shape = RoundedCornerShape(4.dp))
                .height(35.dp),
            onValueChange = {
                textRemember.value = it
            },
            singleLine = true,
            cursorBrush = SolidColor(textFieldColor),
            textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colors.onSurface,
                fontSize = fontSize,
                fontFamily = Fonts.roboto()
            ),
            decorationBox = { innerTextField ->
                Row(
                    modifier.padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (leadingIcon != null) leadingIcon()
                    Box(Modifier.weight(1f)) {
                        if (textRemember.value.isEmpty()) Text(
                            placeholder,
                            style = LocalTextStyle.current.copy(
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.3f),
                                fontSize = fontSize,
                                fontFamily = Fonts.roboto()
                            )
                        )
                        innerTextField()
                    }
                    if (trailingIcon != null) trailingIcon()
                }
            }
        )
    }
}