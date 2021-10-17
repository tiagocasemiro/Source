package br.com.source.viewmodel

import br.com.source.model.git.Executor

class SelectRepositoryViewModel(private val executor: Executor) {

    fun status(): String {
        return executor.fullStatus()
    }
}