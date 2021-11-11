package br.com.source.view.dashboard.right

import br.com.source.model.domain.LocalRepository
import br.com.source.model.service.GitService
import br.com.source.model.util.Message
import br.com.source.view.model.Diff
import br.com.source.view.model.Stash
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent

class RightContainerViewModel(localRepository: LocalRepository) {
    private val gitService: GitService =
        KoinJavaComponent.get(GitService::class.java) { parametersOf(localRepository.fileWorkDir()) }

    fun stashDiff(stash: Stash): Message<List<Diff>> {
        return gitService.stashDiff(stash.objectId)
    }
}