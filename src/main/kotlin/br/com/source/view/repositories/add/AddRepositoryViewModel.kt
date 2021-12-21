package br.com.source.view.repositories.add

import br.com.source.model.database.LocalRepositoryDatabase
import br.com.source.model.domain.LocalRepository
import br.com.source.model.domain.RemoteRepository
import br.com.source.model.service.GitCloneService
import br.com.source.model.util.Message

class AddRepositoryViewModel(private val localRepositoryDatabase: LocalRepositoryDatabase) {

    fun add(localRepository: LocalRepository): Message<Unit> {
        localRepositoryDatabase.save(localRepository)

        return Message.Success(obj = Unit)
    }

    fun clone(remoteRepository: RemoteRepository): Message<Unit> {
        var result = GitCloneService().clone(remoteRepository)
        if(result.isSuccess()) {
            result = add(remoteRepository.localRepository)
        }

        return result
    }
}

