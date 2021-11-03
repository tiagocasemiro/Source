package br.com.source.view.repositories.add

import br.com.source.model.database.LocalRepositoryDatabase
import br.com.source.model.domain.LocalRepository
import br.com.source.model.domain.RemoteRepository
import br.com.source.model.service.GitService
import br.com.source.model.util.Message
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.get

class AddRepositoryViewModel(private val localRepositoryDatabase: LocalRepositoryDatabase) {

    fun add(localRepository: LocalRepository): Message<Unit> {
        localRepositoryDatabase.save(localRepository)

        return Message.Success()
    }

    fun clone(remoteRepository: RemoteRepository): Message<Unit> {
        val gitService: GitService = get(GitService::class.java) { parametersOf(remoteRepository.localRepository.workDir) }
        var result = gitService.clone(remoteRepository)
        if(result.isSuccess()) {
            result = add(remoteRepository.localRepository)
        }

        return result
    }
}

