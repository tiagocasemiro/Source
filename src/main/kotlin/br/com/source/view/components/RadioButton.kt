package br.com.source.view.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.source.view.common.Fonts
import br.com.source.view.common.itemRepositoryText

@Composable
fun SourceRadioButton(label: String, selected: MutableState<String>){
    Row(
        Modifier.clickable {
            selected.value = label
        }.height(33.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected.value == label, onClick = { selected.value = label })
        Spacer(Modifier.size(3.dp))
        Text(
            text = label,
            modifier = Modifier.fillMaxWidth(),
            fontFamily = Fonts.roboto(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = itemRepositoryText
        )
    }
}