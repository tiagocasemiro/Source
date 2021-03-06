package br.com.source.view.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.source.view.common.Fonts
import br.com.source.view.common.InfoColor
import br.com.source.view.common.StatusStyle.negativeButtonColor
import br.com.source.view.common.itemRepositoryText

@Composable
fun SourceRadioButton(label: String, selected: MutableState<String>, emphasis: Boolean = false){
    Row(
        Modifier.clickable {
            selected.value = label
        }.height(33.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.size(10.dp))
        RadioButton(
            selected = selected.value == label,
            onClick = null,
            modifier = Modifier.size(8.dp),
            colors = RadioButtonDefaults.colors(
                selectedColor = InfoColor.color,
                unselectedColor = negativeButtonColor.copy(alpha = 0.6f),
                disabledColor = negativeButtonColor.copy(alpha = ContentAlpha.disabled)
            )
        )
        Spacer(Modifier.size(15.dp))
        Text(
            text = buildAnnotatedString {
                if(emphasis) {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(label)
                    }
                } else {
                    append(label)
                }
            },
            modifier = Modifier.fillMaxWidth().weight(1f),
            fontFamily = Fonts.roboto(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = itemRepositoryText
        )
    }
}