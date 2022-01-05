package br.com.source.view.repositories.edit

import br.com.source.model.database.LocalRepositoryDatabase
import br.com.source.model.domain.LocalRepository
import br.com.source.model.util.Message

class EditRepositoryViewModel(private val localRepositoryDatabase: LocalRepositoryDatabase) {
    fun update(localRepository: LocalRepository): Message<Unit> {
        localRepositoryDatabase.update(localRepository)

        return Message.Success(obj = Unit)
    }
}
