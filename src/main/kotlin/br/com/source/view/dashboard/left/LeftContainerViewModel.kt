package br.com.source.view.dashboard.left

import br.com.source.model.domain.LocalRepository
import br.com.source.model.service.GitService
import br.com.source.view.model.Branch
import br.com.source.view.model.Stash
import br.com.source.view.model.Tag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.get

class LeftContainerViewModel(private val localRepository: LocalRepository) {
    private val gitService: GitService = get(GitService::class.java) { parametersOf(localRepository.fileWorkDir()) }
    private val coroutine = CoroutineScope(Dispatchers.IO)
    private val _localBranchesStatus = MutableStateFlow<List<Branch>>(emptyList())
    val localBranchesStatus: StateFlow<List<Branch>> = _localBranchesStatus
    private val _remoteBranchesStatus = MutableStateFlow<List<Branch>>(emptyList())
    val remoteBranchesStatus: StateFlow<List<Branch>> = _remoteBranchesStatus
    private val _tagsStatus = MutableStateFlow<List<Tag>>(emptyList())
    val tagsStatus: StateFlow<List<Tag>> = _tagsStatus
    private val _stashsStatus = MutableStateFlow<List<Stash>>(emptyList())
    val stashsStatus: StateFlow<List<Stash>> = _stashsStatus
    private val _showLoad = MutableStateFlow(false)
    val showLoad: StateFlow<Boolean> = _showLoad

    fun localBranches() {
        _showLoad.value = true
        coroutine.async {
            gitService.localBranches().onSuccess { localBranches ->
                _localBranchesStatus.value = localBranches
            }
            _showLoad.value = false
        }.start()
    }

    fun remoteBranches() {
        _showLoad.value = true
        coroutine.async {
            gitService.remoteBranches().onSuccess { remoteBranches ->
                _remoteBranchesStatus.value = remoteBranches
            }
            _showLoad.value = false
        }.start()
    }

    fun tags() {
        _showLoad.value = true
        coroutine.async {
            gitService.tags().onSuccess { tags ->
                _tagsStatus.value = tags
            }
            _showLoad.value = false
        }.start()
    }

    fun stashs() {
        _showLoad.value = true
        coroutine.async {
            gitService.stashs().onSuccess { stashs ->
                _stashsStatus.value = stashs
            }
            _showLoad.value = false
        }.start()
    }

    fun deleteLocalBranch(branch: Branch, onSuccess: () -> Unit) {
        _showLoad.value = true
        coroutine.async {
            gitService.deleteLocalBranch(branch.clearName).onSuccess {
                localBranches()
                remoteBranches()
                onSuccess()
            }
            _showLoad.value = false
        }.start()
    }

    fun checkoutLocalBranch(branch: Branch, onSuccess: () -> Unit) {
        _showLoad.value = true
        coroutine.async {
            gitService.checkoutLocalBranch(branch.clearName).onSuccess {
                localBranches()
                remoteBranches()
                onSuccess()
            }
            _showLoad.value = false
        }.start()
    }

    fun checkoutRemoteBranch(branch: Branch, onSuccess: () -> Unit) {
        _showLoad.value = true
        coroutine.async {
            gitService.checkoutRemoteBranch(branch.clearName).onSuccess {
                localBranches()
                remoteBranches()
                onSuccess()
            }
            _showLoad.value = false
        }.start()
    }

    fun deleteRemoteBranch(branch: Branch, onSuccess: () -> Unit) {
        _showLoad.value = true
        coroutine.async {
            gitService.deleteRemoteBranch(branch.clearName).onSuccess {
                remoteBranches()
                onSuccess()
            }
            _showLoad.value = false
        }.start()
    }

    fun isLocalBranch(branch: Branch): Boolean {
        return localBranchesStatus.value.find {
            it.clearName == branch.clearName
        } != null
    }

    fun checkoutTag(tag: Tag, onSuccess: (String) -> Unit) {
        _showLoad.value = true
        coroutine.async {
            gitService.checkoutTag(tag.objectId).onSuccess {
                localBranches()
                remoteBranches()
                onSuccess(it)
            }
            _showLoad.value = false
        }.start()
    }

    fun delete(tag: Tag, onSuccess: (String) -> Unit) {
        _showLoad.value = true
        coroutine.async {
            gitService.deleteTag(tag.name).onSuccess {
                tags()
                onSuccess(it)
            }
            _showLoad.value = false
        }.start()
    }

    fun applyStash(stash: Stash, onSuccess: () -> Unit) {
        _showLoad.value = true
        coroutine.async {
            gitService.applyStash(stash.originalName).onSuccess {
                onSuccess()
            }
            _showLoad.value = false
        }.start()
    }

    fun delete(stash: Stash, onSuccess: () -> Unit) {
        coroutine.async {
            gitService.deleteStash(stash.index).onSuccess {
                stashs()
                onSuccess()
            }
        }.start()
    }
}