package br.com.source.model.util

import br.com.source.view.common.hideLoad
import br.com.source.view.components.showActionError
import br.com.source.view.components.showActionWarn

sealed class Message<T>(val message: String) {
    class Warn<T>(msg: String): Message<T>(msg)
    class Error<T>(msg: String? = null): Message<T>(if(msg.isNullOrEmpty()) generalError() else msg)
    class Success<T>(msg: String? = null, val obj: T): Message<T>(msg?: generalSuccess())

    fun isSuccess(): Boolean {
        return this is Success<*>
    }

    fun isError(): Boolean {
        return this is Error<*>
    }

    fun isWarn(): Boolean {
        return this is Warn<*>
    }

    fun retryOr(replacement: T): T {
        return if (this is Success) obj else replacement
    }

    fun retryOrNull(): T? {
        return if (this is Success) obj else null
    }

    fun on(error: (Message<T>) -> Unit = {}, success: (T) -> Unit = {}, warn: (Message<T>) -> Unit = {}) {
        if(this is Success<T>) {
            success(obj)
        }
        if(isError()) {
            error(this)
        }
        if(isWarn()) {
            warn(this)
        }
    }

    fun onSuccess(on: (T) -> Unit = {}) {
        if(this is Success<T>) {
            on(obj)
        } else {
            this.noSuccess()
        }
    }

    fun onSuccessWithDefaultError(success: (T) -> Unit) {
        on(error = {
            showActionError(it)
            hideLoad()
        },
        success = success)
    }

    fun onSuccessWithWarnDefaultError(warn: (Message<T>) -> Unit = {}, success: (T) -> Unit) {
        on(error = {
            showActionError(it)
            hideLoad()
        },
        success = success,
        warn = warn)
    }

    fun noSuccess() {
        if(isError()) {
            showActionError(this)
            hideLoad()
        }
        if(isWarn()) {
            showActionWarn(this)
            hideLoad()
        }
    }
}