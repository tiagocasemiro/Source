package br.com.source.viewmodel

import br.com.source.model.domain.LocalRepository
import br.com.source.model.git.Executor

class AllRepositoriesViewModel(private val executor: Executor) {

    fun status(): String {
        return executor.fullStatus()
    }

    fun allRepositories(): List<LocalRepository> {
        return emptyList()
    }
}