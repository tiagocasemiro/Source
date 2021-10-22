package br.com.source.viewmodel

import br.com.source.model.database.LocalRepositoryDatabase
import br.com.source.model.domain.LocalRepository
import br.com.source.model.domain.RemoteRepository
import br.com.source.model.git.Executor
import br.com.source.model.util.Message

class AddRepositoryViewModel(private val localRepositoryDatabase: LocalRepositoryDatabase, private val executor: Executor) {

    fun add(localRepository: LocalRepository): Message {
        localRepositoryDatabase.save(localRepository)

        return Message.Success()
    }

    fun clone(remoteRepository: RemoteRepository): Message {
        var result = executor.clone(remoteRepository)
        if(result.isSuccess()) {
            result = add(remoteRepository.localRepository)
        }

        return result
    }
}

