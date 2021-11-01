package br.com.source.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.source.view.common.Fonts
import br.com.source.view.common.StatusStyle.primaryButtonColor

@Composable
fun SourceButton(label: String, color: Color = primaryButtonColor, onclick: () -> Unit) {
    Box(modifier = Modifier.clickable(onClick = onclick)) {
        Text(
            text = label,
            modifier = Modifier
                .height(25.dp)
                .background(
                    color,
                    RoundedCornerShape(5.dp)
                )
                .padding(horizontal = 20.dp)
                .then(Modifier.wrapContentSize(Alignment.Center)),
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            style = typography.h3.copy(color = Color.White),
            fontFamily = Fonts.roboto()
        )
    }
}