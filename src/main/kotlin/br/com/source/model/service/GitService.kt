package br.com.source.model.service

import br.com.source.model.domain.RemoteRepository
import br.com.source.model.util.Message
import br.com.source.view.model.Stash
import br.com.source.view.model.Tag
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand.ListMode.REMOTE
import org.eclipse.jgit.lib.ProgressMonitor
import org.eclipse.jgit.lib.Ref

class GitService(private val git: Git) {
    fun clone(remoteRepository: RemoteRepository): Message {

        println("Cloning from " + remoteRepository.url + " to " + remoteRepository.localRepository.workDir)

        val result = Git.cloneRepository()
            .setURI(remoteRepository.url)
            .setDirectory(remoteRepository.localRepository.fileWorkDir())
            .setProgressMonitor(object : ProgressMonitor {
                override fun start(totalTasks: Int) {
                    println("Starting work on $totalTasks tasks")
                }

                override fun beginTask(title: String, totalWork: Int) {
                    println("Start $title: $totalWork")
                }

                override fun update(completed: Int) {
                    print("$completed-")
                }

                override fun endTask() {
                    println("Done")
                }

                override fun isCancelled(): Boolean {
                    return false
                }
            }).call()
        println("Having repository: " + result.repository.directory);

        return Message.Success()
    }

    fun localBranches(): List<Ref> {
        return git.branchList().call()
    }

    fun remoteBranches(): List<Ref> {
        return git.branchList().setListMode(REMOTE).call()
    }

    fun tags(): List<Tag> {
        val refs: List<Ref> =  git.tagList().call()
        val tags = refs.map {
            Tag(it.name.split("/").last())
        }

        return tags
    }

    fun stashs(): List<Stash> {
        val listRevCommit = git.stashList().call()

        return listRevCommit.mapIndexed { index, revCommit ->
            val name = revCommit.shortMessage.split(":").takeIf {it.size > 1}?.get(1)?.trimStart()?.trimEnd()
                ?.split(" ").takeIf { it != null && it.size > 1}?.toMutableList().apply {this?.removeFirst()}
                ?.joinToString(separator = " ")?: "stash@{$index}"

            Stash(name)
        }
    }
}
