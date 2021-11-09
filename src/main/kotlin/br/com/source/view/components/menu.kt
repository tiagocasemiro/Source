package br.com.source.view.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.source.model.util.sleep
import br.com.source.view.common.Fonts
import br.com.source.view.common.SourceTooltip
import br.com.source.view.common.itemBranchHoveBackground
import br.com.source.view.common.itemRepositoryText

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TopMenuItem(resourcePath: String, tooltipMessage: String, label: String, onClick: () -> Unit) {
    val backgroundColor = remember { mutableStateOf(Color.Transparent) }
    SourceTooltip(tooltipMessage) {
        Column(
            modifier = Modifier
                .pointerMoveFilter(
                    onExit = {
                        backgroundColor.value = Color.Transparent
                        false
                    },
                    onEnter = {
                        backgroundColor.value = itemBranchHoveBackground
                        false
                    }
                ).clickable {
                    sleep(80) {
                        onClick()
                    }
                }.width(60.dp)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painterResource(resourcePath),
                contentDescription = "Indication of expanded card",
                modifier = Modifier.size(30.dp)
            )
            Spacer(Modifier.size(3.dp))
            Text(
                text = label,
                fontFamily = Fonts.roboto(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = itemRepositoryText,
            )
        }
    }
}
