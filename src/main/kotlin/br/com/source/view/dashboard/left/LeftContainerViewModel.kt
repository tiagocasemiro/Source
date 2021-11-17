package br.com.source.view.dashboard.left

import br.com.source.model.domain.LocalRepository
import br.com.source.model.service.GitService
import br.com.source.model.util.Message
import br.com.source.view.model.Branch
import br.com.source.view.model.Stash
import br.com.source.view.model.Tag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.get

class LeftContainerViewModel(private val localRepository: LocalRepository) {
    private val gitService: GitService = get(GitService::class.java) { parametersOf(localRepository.fileWorkDir()) }
    private val coroutine = CoroutineScope(Dispatchers.IO)

    fun localBranches(message: (Message<List<Branch>>) -> Unit) {
        coroutine.async {
            val obj = gitService.localBranches()
            withContext(Dispatchers.Main) {
                message(obj)
            }
        }.start()
    }

    fun remoteBranches(message: (Message<List<Branch>>) -> Unit) {
        coroutine.async {
            val obj = gitService.remoteBranches()
            withContext(Dispatchers.Main) {
                message(obj)
            }
        }.start()
    }

    fun tags(message: (Message<List<Tag>>) -> Unit) {
        coroutine.async {
            val obj = gitService.tags()
            withContext(Dispatchers.Main) {
                message(obj)
            }
        }.start()
    }

    fun stashs(message: (Message<List<Stash>>) -> Unit) {
        coroutine.async {
            val obj = gitService.stashs()
            withContext(Dispatchers.Main) {
                message(obj)
            }
        }.start()
    }

    fun deleteLocalBranch(branch: Branch, message: (Message<Unit>) -> Unit) {
        coroutine.async {
            val obj = gitService.deleteLocalBranch(branch.clearName)
            withContext(Dispatchers.Main) {
                message(obj)
            }
        }.start()
    }

    fun deleteRemoteBranch(branch: Branch, message: (Message<Unit>) -> Unit) {
        coroutine.async {
            val obj = gitService.deleteRemoteBranch(branch.clearName)
            withContext(Dispatchers.Main) {
                message(obj)
            }
        }.start()
    }

    fun checkoutLocalBranch(branch: Branch, message: (Message<Unit>) -> Unit) {
        coroutine.async {
            val obj = gitService.checkoutLocalBranch(branch.clearName)
            withContext(Dispatchers.Main) {
                message(obj)
            }
        }.start()
    }

    fun checkoutRemoteBranch(branch: Branch, message: (Message<Unit>) -> Unit) {
        coroutine.async {
            val obj = gitService.checkoutRemoteBranch(branch.clearName)
            withContext(Dispatchers.Main) {
                message(obj)
            }
        }.start()
    }

    fun isLocalBranch(branch: Branch, branches: List<Branch>): Boolean {
        for(it in branches)
            if(it.clearName == branch.clearName)
                return true

        return false
    }

    fun checkoutTag(tag: Tag, message: (Message<String>) -> Unit) {
        coroutine.async {
            val obj = gitService.checkoutTag(tag.objectId)
            withContext(Dispatchers.Main) {
                message(obj)
            }
        }.start()
    }

    fun delete(tag: Tag, message: (Message<String>) -> Unit) {
        coroutine.async {
            val obj = gitService.deleteTag(tag.name)
            withContext(Dispatchers.Main) {
                message(obj)
            }
        }.start()
    }

    fun applyStash(stash: Stash, message: (Message<Unit>) -> Unit) {
        coroutine.async {
            val obj = gitService.applyStash(stash.originalName)
            withContext(Dispatchers.Main) {
                message(obj)
            }
        }.start()
    }

    fun delete(stash: Stash, message: (Message<Unit>) -> Unit) {
        coroutine.async {
            val obj = gitService.deleteStash(stash.index)
            withContext(Dispatchers.Main) {
                message(obj)
            }
        }.start()
    }
}