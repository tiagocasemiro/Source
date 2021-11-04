package br.com.source.model.util

import androidx.compose.runtime.MutableState

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