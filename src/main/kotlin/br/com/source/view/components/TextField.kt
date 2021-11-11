package br.com.source.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.source.model.util.conditional
import br.com.source.model.util.emptyString
import br.com.source.view.common.Fonts
import br.com.source.view.common.StatusStyle.backgroundColor
import br.com.source.view.common.StatusStyle.textFieldColor

@Composable
fun SourceTextField(
    text: MutableState<String>,
    label: String = "",
    placeholder: String = "",
    fontSize: TextUnit = 13.sp,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    isPassword: Boolean = false,
    requestFocus: Boolean = false,
    errorMessage:  MutableState<String> = mutableStateOf(emptyString())
) {
    val modifier = Modifier.background(backgroundColor)
    val visualTransformation: VisualTransformation = if(isPassword) PasswordVisualTransformation() else VisualTransformation.None
    val focusRequester = remember { FocusRequester() }
    Column (
        modifier = Modifier.height(60.dp)
    ){
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
            value = text.value,
            modifier = modifier
                .background(
                    MaterialTheme.colors.surface,
                    MaterialTheme.shapes.small,
                )
                .border(width = 1.dp, color = if(errorMessage.value.isEmpty() ) textFieldColor else Color.Red, shape = RoundedCornerShape(4.dp))
                .height(35.dp)
                .conditional(requestFocus, ifTrue = { it.focusRequester(focusRequester) }, ifFalse = { it }),
            onValueChange = {
                text.value = it
            },
            singleLine = true,
            cursorBrush = SolidColor(textFieldColor),
            textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colors.onSurface,
                fontSize = fontSize,
                fontFamily = Fonts.roboto(),
            ),
            decorationBox = { innerTextField ->
                Row(
                    modifier.padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (leadingIcon != null) leadingIcon()
                    Box(Modifier.weight(1f)) {
                        if (text.value.isEmpty()) Text(
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
            },
            visualTransformation = visualTransformation,
        )
        if(errorMessage.value.isNotEmpty()) {
            Text(
                errorMessage.value,
                style = LocalTextStyle.current.copy(
                    color = Color.Red.copy(alpha = 0.8f),
                    fontSize = (fontSize.value - 3f).sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Fonts.roboto()
                )
            )
        }
    }
    if(requestFocus) {
        DisposableEffect(Unit) {
            focusRequester.requestFocus()
            onDispose { }
        }
    }
}