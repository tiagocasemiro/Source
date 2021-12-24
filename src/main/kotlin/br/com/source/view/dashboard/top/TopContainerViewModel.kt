package br.com.source.view.dashboard.top

import br.com.source.model.domain.LocalRepository
import br.com.source.model.service.GitService
import br.com.source.model.util.Message
import br.com.source.view.model.Branch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class TopContainerViewModel(localRepository: LocalRepository) {
    private val gitService: GitService = GitService(localRepository)
    private val coroutine = CoroutineScope(Dispatchers.IO)

    fun createStash(messageStash: String, message: (Message<Unit>) -> Unit) {
        coroutine.async {
            val obj = gitService.createStash(messageStash)
            withContext(Dispatchers.Main) {
                message(obj)
            }
        }.start()
    }

    fun localBranches(message: (Message<List<Branch>>) -> Unit) {
        coroutine.async {
            val obj = gitService.localBranches()
            withContext(Dispatchers.Main) {
                message(obj)
            }
        }.start()
    }

    fun merge(selectedBranch: String, messageMerge: String? = null, message: (Message<Unit>) -> Unit) {
        coroutine.async {
            val obj = gitService.merge(selectedBranch, messageMerge)
            withContext(Dispatchers.Main) {
                message(obj)
            }
        }.start()
    }

    fun createNewBranch(name: String, switchToNewBranch: Boolean, message: (Message<Unit>) -> Unit) {
        coroutine.async {
            val obj = gitService.createNewBranch(name, switchToNewBranch)
            withContext(Dispatchers.Main) {
                message(obj)
            }
        }.start()
    }

    fun fetch(message: (Message<String>) -> Unit) {
        coroutine.async {
            val obj = gitService.fetch()
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

    fun pull(branch: String, message: (Message<Unit>) -> Unit) {
        coroutine.async {
            val obj = gitService.pull(branch)
            withContext(Dispatchers.Main) {
                message(obj)
            }
        }.start()
    }

    fun push(message: (Message<Unit>) -> Unit) {
        coroutine.async {
            val obj = gitService.push()
            withContext(Dispatchers.Main) {
                message(obj)
            }
        }.start()
    }
}