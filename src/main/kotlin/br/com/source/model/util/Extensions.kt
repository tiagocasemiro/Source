package br.com.source.model.util

import androidx.compose.foundation.ContextMenuState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.PressGestureScope
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.consumeDownChange
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput

fun emptyString() = ""

fun errorOn(text: String) = "Error on: $text. \n" +
        "Try to validate your repository in the terminal."

fun generalError() = errorOn("An internal error has occurred")

fun generalSuccess() = "Command executed successfully"

fun MutableState<String>.validation(validations: List<(String) -> Boolean>, errorMessage:  MutableState<String>, messageError: String): Boolean {
    for (validate in validations) {
        if(validate(this.value)) {
            errorMessage.value = messageError
            return false
        }
    }
    errorMessage.value = emptyString()

    return true
}

fun emptyValidation() = { it: String ->
    it.isEmpty()
}

fun <T>tryCatch(block: () -> Message<T>): Message<T> {
    return try {
        block()
    } catch (e: Exception) {
        Message.Error(e.message)
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.detectTapGesturesWithContextMenu(onDoubleTap: (Offset) -> Unit = {},
                                  onLongPress: (Offset) -> Unit = {},
                                  onPress: suspend PressGestureScope.(Offset) -> Unit = {},
                                  onTap: (Offset) -> Unit = {},
                                  state: ContextMenuState): Modifier {
    return this.pointerInput(Unit) {
        detectTapGestures(
            onDoubleTap = onDoubleTap,
            onTap = onTap,
            onLongPress = onLongPress,
            onPress = onPress
        )
    }
    .pointerInput(state) {
        forEachGesture {
            awaitPointerEventScope {
                val event = awaitPointerEvent()
                if (event.buttons.isSecondaryPressed) {
                    event.changes.forEach { it.consumeDownChange() }
                    state.status =
                        ContextMenuState.Status.Open(Rect(event.changes[0].position, 0f))
                }
            }
        }
    }
}