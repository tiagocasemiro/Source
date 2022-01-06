package br.com.source.model.util

import androidx.compose.foundation.ContextMenuState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun emptyString() = ""

fun generalError() = "An internal error has occurred"

fun generalSuccess() = "Command executed successfully"

fun MutableState<String>.validation(validations: List<(String) -> String>, errorMessage:  MutableState<String>): Boolean {
    for (validate in validations) {
        val messageError = validate(this.value)
        if(messageError.isNotEmpty()) {
            errorMessage.value = messageError
            return false
        }
    }
    errorMessage.value = emptyString()

    return true
}

fun emptyValidation(message: String) = { it: String ->
    check(it.isEmpty(), message)
}

fun containSpacesValidation(message: String) = { it: String ->
    check(it.trim().contains(" "), message)
}

private fun check(isNotValid: Boolean, message: String): String {
    return if(isNotValid) message else emptyString()
}

fun <T>tryCatch(block: () -> Message<T>): Message<T> {
    return try {
        block()
    } catch (e: Exception) {
        e.printStackTrace()
        Message.Error("${e.message}\n\n${e.cause?.message?:emptyString()}")
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

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.detectClickWithContextMenu(onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    onClick: () -> Unit,
    state: ContextMenuState): Modifier {
    return this.combinedClickable(
        onLongClick = onLongClick,
        onDoubleClick = onDoubleClick,
        onClick = onClick
    )
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

fun Modifier.conditional(condition: Boolean, ifTrue: (Modifier) -> Modifier, ifFalse: (Modifier) -> Modifier): Modifier {
    return if(condition) ifTrue(this) else ifFalse(this)
}

fun sleep(delay: Long, execute: () -> Unit) {
    CoroutineScope(Dispatchers.Main).launch {
        delay(delay)
        execute()
    }
}

fun <T> MutableList<T>.clone(): MutableList<T> {
    val temp = mutableListOf<T>()
    temp.addAll(this)

    return temp
}

fun <T> List<T>.indexOfFirstOrNull(predicate: (T) -> Boolean): Int? {
    for ((index, item) in this.withIndex()) {
        if (predicate(item))
            return index
    }
    return null
}