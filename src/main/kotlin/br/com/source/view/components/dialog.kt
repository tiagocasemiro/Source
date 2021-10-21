package br.com.source.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowSize
import androidx.compose.ui.window.rememberDialogState
import br.com.source.view.common.Fonts
import br.com.source.view.common.StatusStyle.Companion.backgroundColor
import br.com.source.view.common.StatusStyle.Companion.negativeButtonColor
import br.com.source.view.status

@ExperimentalMaterialApi
@Composable
fun SourceDialog(
    close: () -> Unit,
    content: @Composable () -> Unit,
    title: String,
    positiveAction: () -> Unit,
    positiveLabel: String,
    negativeAction: () -> Unit = {},
    negativeLabel: String? = null) {
    AlertDialog(
        onDismissRequest = close ,
        modifier = Modifier.width(350.dp).height(200.dp).background(backgroundColor),
        confirmButton = {
            Row {
                if(negativeLabel != null) {
                    SourceButton(negativeLabel, color = negativeButtonColor) {
                        negativeAction()
                        close()
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }
                SourceButton(positiveLabel) {
                    positiveAction()
                    close()
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(title,
                    textAlign = TextAlign.Start,
                    fontFamily = Fonts.balooBhai2(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.size(20.dp))
                content()
                Spacer(modifier = Modifier.fillMaxSize())
            }
        },
    )
}

@Composable
fun SourceWindowDialog(close: () -> Unit, titleWindow: String, size: WindowSize = WindowSize(400.dp, 300.dp), content: @Composable () -> Unit,) {
    Dialog(
        state = rememberDialogState(
            position = WindowPosition(Alignment.Center),
            size = size,
        ),
        onCloseRequest = { close() },
        title = titleWindow,
        content = { content() }
    )
}