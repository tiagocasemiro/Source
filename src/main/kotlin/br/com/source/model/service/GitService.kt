package br.com.source.model.service

import br.com.source.model.domain.RemoteRepository
import br.com.source.model.util.Message
import br.com.source.model.util.errorOn
import br.com.source.model.util.tryCatch
import br.com.source.view.model.Branch
import br.com.source.view.model.Stash
import br.com.source.view.model.Tag
import org.eclipse.jgit.api.CreateBranchCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand.ListMode.REMOTE
import org.eclipse.jgit.api.Status
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.ProgressMonitor
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revwalk.RevWalk

class GitService(private val git: Git) {

    fun clone(remoteRepository: RemoteRepository): Message<Unit> {

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

        return Message.Success(obj = Unit)
    }

    fun localBranches(): Message<List<Branch>> {
        return try {
            val refs = git.branchList().call()
            Message.Success(obj = refs.map {
                Branch(fullName = it.name, isCurrent = it.name == git.repository.fullBranch)
            })
        } catch (e: Exception) {
            Message.Error(errorOn("Load local branches"))
        }
    }

    fun remoteBranches(): Message<List<Branch>> {
        val refs = git.branchList().setListMode(REMOTE).call()
        return try {
            Message.Success(obj = refs.map {
                Branch(fullName = it.name)
            })
        } catch (e: Exception) {
            Message.Error(errorOn("Load remote branches"))
        }
    }

    fun tags(): List<Tag> {
        val refs: List<Ref> =  git.tagList().call()
        val tags = refs.map {
            Tag(it.name.split("/").last(), it.objectId)
        }

        return tags
    }

    fun stashs(): List<Stash> {
        val listRevCommit = git.stashList().call()

        return listRevCommit.mapIndexed { index, revCommit ->
            Stash(revCommit.name, revCommit.shortMessage, index)
        }
    }

    fun deleteLocalBranch(name: String): Message<Unit> {
        return try {
            val list = git.branchDelete()
                .setBranchNames(name)
                .setForce(true)
                .call()

           if(list.isEmpty())
               return Message.Error()

            Message.Success(obj = Unit)
        } catch (e: Exception) {
            Message.Error("Delete local branch")
        }
    }

    fun deleteRemoteBranch(name: String): Message<Unit> {
        return try {
            val list = git.branchDelete()
                .setBranchNames("origin/$name")
                .setForce(true)
                .call()

            if(list.isEmpty())
                return Message.Error()

            Message.Success(obj = Unit)
        } catch (e: Exception) {
            Message.Error("Delete remote branch")
        }
    }

    fun checkoutLocalBranch(name: String): Message<Unit> {
        return try {
            git.checkout().setName(name).call()
            Message.Success(obj = Unit)
        } catch (e: Exception) {
            Message.Error("Checkout local branch")
        }
    }

    fun checkoutRemoteBranch(name: String): Message<Unit> {
        return try {
            git.checkout()
                .setCreateBranch(true)
                .setName(name)
                .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                .setStartPoint("origin/$name")
                .call()

            Message.Success(obj = Unit)
        } catch (e: Exception) {
            Message.Error("Checkout remote branch")
        }
    }

    fun checkoutTag(objectId: ObjectId): Message<String> {
        return try {
            val walk = RevWalk(git.repository)
            val commit = walk.parseCommit(objectId)
            if(commit != null) {
                git.checkout()
                    .setStartPoint(commit)
                    .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM)
                    .setName(commit.name)
                    .call()

                return Message.Success(obj ="Checkout at commit ${commit.name} with success")
            }

            Message.Error("Cannot retrieve branch from commit tag")
        } catch (e: Exception) {
            Message.Error("Checkout tag")
        }
    }

    fun deleteTag(name: String): Message<String> {
        return tryCatch {
            git.tagDelete().setTags(name).call()

            Message.Success(obj = "Tag $name deleted with success")
        }
    }

    fun applyStash(name: String): Message<Unit> {
        return tryCatch {
            git.stashApply().setStashRef(name).call()

            Message.Success(obj = Unit)
        }
    }

    fun deleteStash(index: Int): Message<Unit> {
        return tryCatch {
            git.stashDrop().setStashRef(index).call()

            Message.Success(obj = Unit)
        }
    }

    fun createStash(message: String): Message<Unit> {
        return tryCatch {
            val status: Status = git.status().call()
            if (status.uncommittedChanges.size <= 0) {
                return@tryCatch Message.Warn("There are no uncommitted changes.")
            }
            git.stashCreate().setIndexMessage(message).setWorkingDirectoryMessage(message).call()

            Message.Success(obj = Unit)
        }
    }
}
