package br.com.source.model.git

import br.com.source.model.domain.RemoteRepository
import br.com.source.model.util.Message
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.ProgressMonitor

class Executor(private val git: Git) {
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

}
