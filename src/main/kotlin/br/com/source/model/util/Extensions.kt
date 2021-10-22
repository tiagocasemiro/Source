package br.com.source.model.util

import androidx.compose.runtime.MutableState

fun emptyString() = ""

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