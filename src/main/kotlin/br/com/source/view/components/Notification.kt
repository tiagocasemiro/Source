package br.com.source.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import br.com.source.view.common.*

@ExperimentalComposeUiApi
@Composable
fun SourceNotification(notificationData: NotificationData, close: () -> Unit) {
    val onHoverRemoveButton = remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(notificationData.type.on(
                info = { SuccessColor.color },
                warn = { WarnColor.color},
                error = { ErrorColor.color },
                none = { InfoColor.color },
                success = { SuccessColor.color }
            ), RoundedCornerShape(5.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = notificationData.message,
            style = TextStyle(
                color = Color.White,
                fontFamily = Fonts.roboto(),
            ),
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(10.dp))
        Card(
            shape = CircleShape,
            elevation = 0.dp,
            modifier = Modifier
                .pointerMoveFilter(
                    onEnter = {
                        onHoverRemoveButton.value = true
                        false
                    },
                    onExit = {
                        onHoverRemoveButton.value = false
                        false
                    }
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            close()
                        }
                    )
                },
            backgroundColor = notificationData.type.on(
                info = { SuccessColor.color },
                warn = { WarnColor.color},
                error = { ErrorColor.color },
                none = { InfoColor.color },
                success = { SuccessColor.color }
            )
        ) {
            Box(
                modifier = Modifier.background(if (onHoverRemoveButton.value) Color(0,0,0,50) else Color.Transparent)
                    .padding(5.dp)
            ) {
                Image(
                    painter = painterResource("images/delete-repository-icon-white.svg"),
                    contentDescription = "Close notification",
                    modifier = Modifier.size(15.dp),
                )
            }
        }
    }
}