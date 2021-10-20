package br.com.source.view.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SourceButton(label: String, onclick: () -> Unit) {
    Text(
        text = label,
        modifier = Modifier
            .height(25.dp)
            .background(
                MaterialTheme.colors.primary,
                RoundedCornerShape(5.dp)
            )
            .padding(horizontal = 20.dp)
            .then(Modifier.wrapContentSize(Alignment.Center))
            .clickable(onClick = onclick),
        fontSize = 13.sp,
        textAlign = TextAlign.Center,
        style = typography.h3.copy(color = Color.White),
    )
}