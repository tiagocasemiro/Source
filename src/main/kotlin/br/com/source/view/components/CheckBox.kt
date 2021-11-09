package br.com.source.view.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.source.view.common.Fonts
import br.com.source.view.common.InfoColor
import br.com.source.view.common.StatusStyle
import br.com.source.view.common.itemRepositoryText

@Composable
fun SourceCheckBox(label: String, checkedState: MutableState<Boolean>) {
    Row(
        Modifier.clickable {
            checkedState.value = checkedState.value.not()
        }.height(33.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.size(10.dp))
        Checkbox(
            checked = checkedState.value,
            onCheckedChange = null,
            modifier = Modifier.size(15.dp),
            colors = CheckboxDefaults.colors(
                checkedColor = InfoColor.color,
                uncheckedColor = StatusStyle.negativeButtonColor.copy(alpha = 0.6f),
                checkmarkColor = StatusStyle.negativeButtonColor,
                disabledColor = StatusStyle.negativeButtonColor.copy(alpha = ContentAlpha.disabled),
                disabledIndeterminateColor = StatusStyle.negativeButtonColor.copy(alpha = ContentAlpha.disabled)
            )
        )
        Spacer(Modifier.size(15.dp))
        Text(
            text = label,
            modifier = Modifier.fillMaxWidth().weight(1f),
            fontFamily = Fonts.roboto(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = itemRepositoryText
        )
    }
}
