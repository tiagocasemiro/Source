package br.com.source.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import br.com.source.model.util.Message
import br.com.source.model.util.emptyString
import br.com.source.view.common.*
import br.com.source.view.common.StatusStyle.cardTextColor
import br.com.source.view.common.StatusStyle.negativeButtonColor
import br.com.source.view.common.StatusStyle.textFieldColor

enum class TypeCommunication {
    warn, info, error, none, success;

    fun <T>on(info: () -> T, warn: () -> T, error: () -> T, none: () -> T, success: () -> T): T {
        return when(this) {
            TypeCommunication.warn -> warn()
            TypeCommunication.info -> info()
            TypeCommunication.error -> error()
            TypeCommunication.none -> none()
            TypeCommunication.success -> success()
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
    size: DpSize = DpSize(width = 450.dp, height = 250.dp),
    type: TypeCommunication = TypeCommunication.info,
    canClose: MutableState<Boolean> = mutableStateOf(true),
    content: @Composable () -> Unit) {
    Popup(
        alignment = Alignment.Center,
        focusable = true,
        onDismissRequest = close,
    ) {
        Box(contentAlignment = Alignment.Center,) {
            Spacer(Modifier.fillMaxSize().background(Color(0,0,0,80)))
            Box(modifier = Modifier
                .size(size)
                .background(dialogBackgroundColor, RoundedCornerShape(10.dp))
                .border(1.dp, itemRepositoryBackground, RoundedCornerShape(10.dp))
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                    Text(title,
                        textAlign = TextAlign.Start,
                        fontFamily = Fonts.balooBhai2(),
                        fontSize = 23.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = type.on(info = { InfoColor.color }, warn = { WarnColor.color }, error = { ErrorColor.color }, none = { InfoColor.color }, success = { SuccessColor.color })
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
                                close()
                                negativeAction()
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                        SourceButton(positiveLabel, color = type.on(info = { InfoColor.color }, warn = { WarnColor.color }, error = { ErrorColor.color }, none = { StatusStyle.primaryButtonColor }, success = { SuccessColor.color })) {
                            if(canClose.value) {
                                close()
                            }
                            positiveAction()
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
    val title: String = emptyString(),
    val message: String? = null,
    val content: @Composable () -> Unit = {},
    val type: TypeCommunication = TypeCommunication.none,
    val labelPositiveButton: String = "OK",
    val actionPositiveButton: () -> Unit = {},
    val labelNegativeButton: String? = null,
    val actionNegativeButton: () -> Unit = {},
    val canClose: MutableState<Boolean> = mutableStateOf(true),
    val size: DpSize = DpSize(width = 450.dp, height = 250.dp),
    val bottomMessage: String? = null
) {
    var emphasisMessage: List<TextCustom>? = null
}

open class TextCustom(val text: String)
class BoldText(txt: String): TextCustom(txt)
class NormalText(txt: String): TextCustom(txt)

private val errorDialogState = mutableStateOf<DialogBuffer?>(null)

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
            negativeAction = data.actionNegativeButton,
            negativeLabel = data.labelNegativeButton,
            canClose = data.canClose,
            type = data.type,
            size = data.size
        ) {
            if(data.emphasisMessage != null) {
                EmphasisText(data.emphasisMessage!!)
            } else if(data.message == null) {
                data.content()
            } else{
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(data.message,
                        modifier = Modifier.fillMaxSize().weight(1f),
                        style = TextStyle(
                            color = cardTextColor,
                            fontSize = 16.sp,
                            fontFamily = Fonts.roboto(),
                            fontWeight = FontWeight.Normal
                        )
                    )
                    if(data.bottomMessage != null) {
                        Box(
                            modifier = Modifier
                                .background(Color(245, 245, 240),RoundedCornerShape(4.dp))
                                .border(1.dp, textFieldColor, RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.padding( horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Image(
                                    painter = painterResource("images/info_icon.svg"),
                                    contentDescription = "Information icon",
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = data.bottomMessage,
                                    modifier = Modifier.padding(7.dp),
                                    style = TextStyle(
                                        fontFamily = Fonts.roboto(),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        color = textFieldColor
                                    )
                                )
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun EmphasisText(text: List<TextCustom>) {
    Text(
        buildAnnotatedString {
            text.forEach {
                when(it) {
                    is BoldText -> {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("${it.text} ")
                        }
                    }
                    is NormalText -> {
                        append("${it.text} ")
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize(),
        style = TextStyle(
            color = cardTextColor,
            fontSize = 16.sp,
            fontFamily = Fonts.roboto(),
            fontWeight = FontWeight.Normal
        )
    )
}

fun showDialog(title: String, message: String, type: TypeCommunication = TypeCommunication.none, bottomMessage: String? = null) {
    errorDialogState.value = DialogBuffer(title , message, type = type, bottomMessage = bottomMessage)
}

fun showActionError(error: Message<*>) = showDialog("Action Error", error.message, type = TypeCommunication.error, bottomMessage = "Try to validate your repository in the terminal")

fun showActionWarn(warn: Message<*>) = showDialog("Action Warn", warn.message, type = TypeCommunication.warn, )

fun showError(error: Message<*>) = showDialog("Error", error.message, type = TypeCommunication.error)

fun showWarn(warn: Message<*>) = showDialog("Warn", warn.message, type = TypeCommunication.warn)

fun showDialogSingleButton(title: String, message: String, type: TypeCommunication = TypeCommunication.none, bottomMessage: String? = null, label: String = "OK", action: () -> Unit = {}) {
    errorDialogState.value = DialogBuffer(title , message, type = type, actionPositiveButton = action, labelPositiveButton = label, bottomMessage = bottomMessage)
}

fun showDialogTwoButton(
    title: String,
    message: String,
    type: TypeCommunication = TypeCommunication.none,
    labelPositive: String,
    actionPositive: () -> Unit,
    labelNegative: String,
    actionNegative: () -> Unit = {},
) {
    errorDialogState.value = DialogBuffer(title , message, type = type,
        labelPositiveButton = labelPositive,
        actionPositiveButton = actionPositive,
        actionNegativeButton = actionNegative,
        labelNegativeButton = labelNegative)
}

fun showDialogContentTwoButton(
    title: String,
    content: @Composable () -> Unit = {},
    type: TypeCommunication = TypeCommunication.none,
    labelPositive: String,
    actionPositive: () -> Unit,
    labelNegative: String,
    canClose: MutableState<Boolean> = mutableStateOf(true),
    actionNegative: () -> Unit = {},
) {
    errorDialogState.value = DialogBuffer(title , null, content = content, type = type,
        labelPositiveButton = labelPositive,
        actionPositiveButton = actionPositive,
        actionNegativeButton = actionNegative,
        labelNegativeButton = labelNegative,
        canClose = canClose)
}

fun showDialogContentTwoButton(
    title: String,
    content: @Composable () -> Unit = {},
    type: TypeCommunication = TypeCommunication.none,
    labelPositive: String,
    actionPositive: () -> Unit,
    labelNegative: String,
    actionNegative: () -> Unit = {},
    size: DpSize
) {
    errorDialogState.value = DialogBuffer(title , null, content = content, type = type,
        labelPositiveButton = labelPositive,
        actionPositiveButton = actionPositive,
        actionNegativeButton = actionNegative,
        labelNegativeButton = labelNegative,
        size = size)
}

fun showDialogTwoButton(
    title: String,
    message: List<TextCustom>,
    type: TypeCommunication = TypeCommunication.none,
    labelPositive: String,
    actionPositive: () -> Unit,
    labelNegative: String,
    actionNegative: () -> Unit = {},
) {
    val bufferDialog = DialogBuffer(title , "", type = type,
        labelPositiveButton = labelPositive,
        actionPositiveButton = actionPositive,
        actionNegativeButton = actionNegative,
        labelNegativeButton = labelNegative)
    bufferDialog.emphasisMessage = message
    errorDialogState.value = bufferDialog
}

fun hideDialog() {
    errorDialogState.value = null
}