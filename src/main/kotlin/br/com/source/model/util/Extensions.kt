package br.com.source.model.util

import androidx.compose.runtime.MutableState

fun emptyString() = ""

fun MutableState<String>.isEmptyValidationAndApplyErrorMessage(errorMessage:  MutableState<String>, messageError: String): Boolean {
    if(this.value.isEmpty()) {
        errorMessage.value = messageError
        return false
    }
    errorMessage.value = emptyString()

    return true
}