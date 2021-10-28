package br.com.source.view.dashboard.left.branches

import br.com.source.model.domain.LocalRepository
import br.com.source.model.service.GitService
import br.com.source.view.model.Branch
import br.com.source.view.model.Stash
import br.com.source.view.model.Tag
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.get


class BranchesViewModel(private val localRepository: LocalRepository) {

    private val gitService: GitService = get(GitService::class.java) { parametersOf(localRepository.fileWorkDir()) }

    fun localBranches(): List<Branch> {
        return gitService.localBranches()
    }

    fun remoteBranches(): List<Branch> {
        return gitService.remoteBranches()
    }

    fun tags(): List<Tag> {
        return gitService.tags()
    }

    fun stashs(): List<Stash> {
        return  gitService.stashs()
    }
}