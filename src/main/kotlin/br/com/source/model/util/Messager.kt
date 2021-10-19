package br.com.source.model.util

sealed class Messager {
    class Error(val message: String): Messager()
    class Success(val message: String? = null): Messager()
}