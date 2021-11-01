package br.com.source.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import br.com.source.view.common.*
import br.com.source.view.common.StatusStyle.cardTextColor
import br.com.source.view.common.StatusStyle.negativeButtonColor

enum class TypeDialog {
    warn, info, error, none;

    fun <T>on(info: () -> T, warn: () -> T, error: () -> T, none: () -> T): T {
        return when(this) {
            TypeDialog.warn -> warn()
            TypeDialog.info -> info()
            TypeDialog.error -> error()
            TypeDialog.none -> none()
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun SourceDialog(
    close: () -> Unit,
    title: String,
    positiveAction: () -> Unit,
    positiveLabel: String,
    negativeAction: () -> Unit = {},
    negativeLabel: String? = null,
    type: TypeDialog = TypeDialog.info,
    content: @Composable () -> Unit) {
    Popup(
        alignment = Alignment.Center,
        focusable = true,
        onDismissRequest = close,
    ) {
        Box(contentAlignment = Alignment.Center,) {
            Spacer(Modifier.fillMaxSize().background(Color(0,0,0,80)))
            Box(modifier = Modifier
                .width(450.dp)
                .height(250.dp)
                .background(dialogBackgroundColor, RoundedCornerShape(10.dp))
                .border(1.dp, itemRepositoryBackground, RoundedCornerShape(10.dp))
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                    Text(title,
                        textAlign = TextAlign.Start,
                        fontFamily = Fonts.balooBhai2(),
                        fontSize = 23.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = type.on(info = { InfoDialog.color }, warn = { WarnDialog.color }, error = { ErrorDialog.color }, none = { StatusStyle.primaryButtonColor })
                    )
                    Spacer(modifier = Modifier.size(20.dp))
                    Box(modifier = Modifier.fillMaxSize().weight(1f)){
                        content()
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if(negativeLabel != null) {
                            SourceButton(negativeLabel, color = negativeButtonColor) {
                                negativeAction()
                                close()
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                        SourceButton(positiveLabel, color = type.on(info = { InfoDialog.color }, warn = { WarnDialog.color }, error = { ErrorDialog.color }, none = { StatusStyle.primaryButtonColor })) {
                            positiveAction()
                            close()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SourceWindowDialog(close: () -> Unit, titleWindow: String, size: DpSize = DpSize(400.dp, 300.dp), content: @Composable () -> Unit,) {
    DpSize(400.dp, 300.dp)
    Dialog(
        state = rememberDialogState(
            position = WindowPosition(Alignment.Center),
            size = size,
        ),
        onCloseRequest = { close() },
        title = titleWindow,
        content = { content() },
    )
}

data class DialogBuffer(
    val title: String,
    val message: String,
    val type: TypeDialog = TypeDialog.none,
    val labelPositiveButton: String = "OK",
    val actionPositiveButton: () -> Unit = {},
    val labelNegativeButton: String = "",
    val actionNegativeButton: () -> Unit = {},
)

private val errorDialogState = mutableStateOf<DialogBuffer?>(null)

@Composable
fun defaultMessageDialog(message: String) = Text(message,
    modifier = Modifier.fillMaxSize(),
    style = TextStyle(
        color = cardTextColor,
        fontSize = 16.sp,
        fontFamily = Fonts.roboto(),
        fontWeight = FontWeight.Normal
    )
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun createDialog() {
    if(errorDialogState.value != null) {
        val data = errorDialogState.value!!
        SourceDialog(
            close = { errorDialogState.value = null },
            title = data.title,
            positiveAction = data.actionPositiveButton,
            positiveLabel = data.labelPositiveButton,
            type = data.type
        ) {
            defaultMessageDialog(data.message)
        }
    }
}

fun showDialog(title: String, message: String, type: TypeDialog = TypeDialog.none,) {
    errorDialogState.value = DialogBuffer(title , message, type)
}

fun hideDialog() {
    errorDialogState.value = null
}