package br.com.source.viewmodel

import br.com.source.model.database.LocalRepositoryDatabase
import br.com.source.model.domain.LocalRepository
import br.com.source.model.util.Messager

class AddLocalRepositoryViewModel(private val localRepositoryDatabase: LocalRepositoryDatabase) {

    fun add(localRepository: LocalRepository): Messager {
        localRepositoryDatabase.save(localRepository)

        return Messager.Success()
    }
}

