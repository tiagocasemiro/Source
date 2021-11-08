package br.com.source.view.dashboard.top

import br.com.source.model.domain.LocalRepository
import br.com.source.model.service.GitService
import br.com.source.model.util.Message
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.get


class TopContainerViewModel(localRepository: LocalRepository) {
    private val gitService: GitService = get(GitService::class.java) { parametersOf(localRepository.fileWorkDir()) }

    fun createStash(message: String): Message<Unit> {
        return gitService.createStash(message)
    }
}