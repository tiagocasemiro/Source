package br.com.source.model.service

import br.com.source.model.domain.RemoteRepository
import br.com.source.model.util.Message
import br.com.source.model.util.emptyString
import br.com.source.model.util.tryCatch
import br.com.source.view.model.*
import org.eclipse.jgit.api.CreateBranchCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand.ListMode.REMOTE
import org.eclipse.jgit.api.MergeCommand
import org.eclipse.jgit.api.Status
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.lib.*
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevTree
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.AbstractTreeIterator
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.treewalk.filter.PathFilter
import java.io.ByteArrayOutputStream
import java.io.IOException


class GitService(private val git: Git) {

    fun clone(remoteRepository: RemoteRepository): Message<Unit> = tryCatch {
        Git.cloneRepository()
            .setURI(remoteRepository.url)
            .setDirectory(remoteRepository.localRepository.fileWorkDir())
            .setProgressMonitor(object : ProgressMonitor {
                override fun start(totalTasks: Int) {
                    //println("Starting work on $totalTasks tasks")
                }

                override fun beginTask(title: String, totalWork: Int) {
                    //println("Start $title: $totalWork")
                }

                override fun update(completed: Int) {
                    //print("$completed-")
                }

                override fun endTask() {
                    //println("Done")
                }

                override fun isCancelled(): Boolean {
                    return false
                }
            }).call()

        Message.Success(obj = Unit)
    }

    fun localBranches(): Message<List<Branch>> = tryCatch {
        val refs = git.branchList().call()
        Message.Success(obj = refs.map {
            Branch(fullName = it.name, isCurrent = it.name == git.repository.fullBranch)
        })
    }

    fun remoteBranches(): Message<List<Branch>> = tryCatch  {
        val refs = git.branchList().setListMode(REMOTE).call()

        Message.Success(obj = refs.map {
            Branch(fullName = it.name,
                isCurrent = it.name.replaceFirst("refs/remotes/origin/", emptyString()) ==
                        git.repository.fullBranch.replaceFirst("refs/heads/", emptyString()))
        })
    }

    fun tags(): Message<List<Tag>> = tryCatch {
        val refs: List<Ref> =  git.tagList().call()
        val tags = refs.map {
            Tag(it.name.split("/").last(), it.objectId)
        }

        Message.Success(obj = tags)
    }

    fun stashs(): Message<List<Stash>> = tryCatch {
        val listRevCommit = git.stashList().call()
        Message.Success(obj = listRevCommit.mapIndexed { index, revCommit ->
            Stash(
                originalName = revCommit.name,
                shortMessage = revCommit.shortMessage,
                index = index,
                objectId = revCommit.toObjectId().name,
            )
        })
    }

    fun deleteLocalBranch(name: String): Message<Unit> = tryCatch {
        val list = git.branchDelete()
            .setBranchNames(name)
            .setForce(true)
            .call()

       if(list.isEmpty())
           return@tryCatch Message.Error()

        Message.Success(obj = Unit)
    }

    fun deleteRemoteBranch(name: String): Message<Unit> = tryCatch {
        val list = git.branchDelete()
            .setBranchNames("origin/$name")
            .setForce(true)
            .call()

        if(list.isEmpty())
            return@tryCatch Message.Error()

        Message.Success(obj = Unit)
    }

    fun checkoutLocalBranch(name: String): Message<Unit> = tryCatch {
        git.checkout().setName(name).call()
        Message.Success(obj = Unit)
    }

    fun checkoutRemoteBranch(name: String): Message<Unit> = tryCatch {
        git.checkout()
            .setCreateBranch(true)
            .setName(name)
            .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
            .setStartPoint("origin/$name")
            .call()

        Message.Success(obj = Unit)
    }

    fun checkoutTag(objectId: ObjectId): Message<String> = tryCatch {
        val walk = RevWalk(git.repository)
        val commit = walk.parseCommit(objectId) ?:
            return@tryCatch Message.Error("Cannot retrieve branch from commit tag")

        git.checkout()
            .setStartPoint(commit)
            .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM)
            .setName(commit.name)
            .call()

        Message.Success(obj ="Checkout at commit ${commit.name} with success")
    }

    fun deleteTag(name: String): Message<String> = tryCatch {
        git.tagDelete().setTags(name).call()

        Message.Success(obj = "Tag $name deleted with success")
    }

    fun applyStash(name: String): Message<Unit> = tryCatch {
        git.stashApply().setStashRef(name).call()

        Message.Success(obj = Unit)
    }

    fun deleteStash(index: Int): Message<Unit> = tryCatch {
        git.stashDrop().setStashRef(index).call()

        Message.Success(obj = Unit)
    }

    fun createStash(message: String): Message<Unit> = tryCatch {
        val status: Status = git.status().call()
        if (status.uncommittedChanges.size <= 0) {
            return@tryCatch Message.Warn("There are no uncommitted changes.")
        }
        git.stashCreate().setIndexMessage(message).setWorkingDirectoryMessage(message).call()

        Message.Success(obj = Unit)
    }

    fun merge(selectedBranch: String, message: String? = null): Message<Unit> = tryCatch {
        val mergeBase: ObjectId = git.repository.resolve(selectedBranch)
        val result = git.merge()
            .include(mergeBase)
            .setCommit(true)
            .setFastForward(MergeCommand.FastForwardMode.NO_FF)
            .setMessage(message)
            .call()
        if(result.mergeStatus.isSuccessful.not()) {

            return@tryCatch Message.Error("Merge status: " + result.mergeStatus.toString() + ".\nResult message: " + result.toString())
        }

        Message.Success(obj = Unit)
    }

    fun createNewBranch(name: String, switchToNewBranch: Boolean): Message<Unit> = tryCatch {
        git.branchCreate()
            .setName(name)
            .call();

        if(switchToNewBranch) {
            return@tryCatch checkoutLocalBranch(name)
        }

        Message.Success(obj = Unit)
    }

    fun fetch(): Message<String> = tryCatch {
        val result = git.fetch().setCheckFetchedObjects(true).call()

        Message.Success(obj = result.messages)
    }

    fun pull(branch: String): Message<Unit> = tryCatch {
        git.pull()
            .setRemote("origin")
            .setRemoteBranchName(branch)
            .call()

        Message.Success(obj = Unit)
    }

    fun push(): Message<Unit> = tryCatch {
        git.push().call()

        Message.Success(obj = Unit)
    }

    fun stashDiff(objectId: String): Message<List<Diff>> = tryCatch {
        val newTreeParser = prepareTreeParserByObjectId(git.repository, objectId)
        val oldTreeParser = prepareTreeParserByObjectId(git.repository,  getParentId(objectId))
        val diff = git.diff().setNewTree(newTreeParser).setOldTree(oldTreeParser).call()
        val diffs = mutableListOf<Diff>()
        for (entry in diff) {
            val out = ByteArrayOutputStream(128)
            val formatter = DiffFormatter(out)
            formatter.setRepository(git.repository)
            formatter.format(entry)
            formatter.flush()
            val fileName = when (entry.changeType.name) {
                DiffEntry.ChangeType.ADD.name -> entry.newPath
                DiffEntry.ChangeType.COPY.name -> entry.oldPath + "->" + entry.newPath
                DiffEntry.ChangeType.DELETE.name -> entry.oldPath
                DiffEntry.ChangeType.MODIFY.name -> entry.oldPath
                DiffEntry.ChangeType.RENAME.name -> entry.oldPath + "->" + entry.newPath
                else -> entry.oldPath + "->" + entry.newPath
            }
            diffs.add(Diff(
                changeType = entry.changeType,
                fileName = fileName,
                content = out.toString()
            ))
        }

        Message.Success(obj = diffs)
    }

    private fun getParentId(id: String): String {
        val lastCommitId: ObjectId = git.repository.resolve(id)
        val revWalk = RevWalk(git.repository)
        val commit = revWalk.parseCommit(lastCommitId)

        return commit.getParent(0).name
    }

    @Throws(IOException::class)
    private fun prepareTreeParserByObjectId(repository: Repository, objectId: String): AbstractTreeIterator {
        RevWalk(repository).use { walk ->
            val commit: RevCommit = walk.parseCommit(ObjectId.fromString(objectId))
            val tree: RevTree = walk.parseTree(commit.tree.id)
            val treeParser = CanonicalTreeParser()
            repository.newObjectReader().use { reader -> treeParser.reset(reader, tree.id) }
            walk.dispose()
            return treeParser
        }
    }

    @Throws(IOException::class)
    private fun prepareTreeParserByBranch(repository: Repository, ref: String): AbstractTreeIterator {
        val head = repository.exactRef(ref)
        RevWalk(repository).use { walk ->
            val commit = walk.parseCommit(head.objectId)
            val tree = walk.parseTree(commit.tree.id)
            val treeParser = CanonicalTreeParser()
            repository.newObjectReader().use { reader -> treeParser.reset(reader, tree.id) }
            walk.dispose()

            return treeParser
        }
    }

    fun unCommittedChanges(): Message<StatusToCommit> = tryCatch {
        val status = git.status().call()
        val conflicting = status.conflicting.map {
            FileCommit(name = it, changeType = DiffEntry.ChangeType.MODIFY, isConflict = true)
        }
        val added = status.added.map {
            FileCommit(name = it, changeType =   DiffEntry.ChangeType.ADD)
        }
        val changed = status.changed.map {
            FileCommit(name = it, changeType = DiffEntry.ChangeType.MODIFY)
        }
        val missing = status.missing.map {
            FileCommit(name = it, changeType = DiffEntry.ChangeType.DELETE)
        }
        val modified = status.modified.map {
            FileCommit(name = it, changeType = DiffEntry.ChangeType.MODIFY)
        }
        val removed = status.removed.map {
            FileCommit(name = it, changeType = DiffEntry.ChangeType.DELETE)
        }
        val untracked = status.untracked.map {
            FileCommit(name = it, changeType = DiffEntry.ChangeType.ADD)
        }.toMutableList().apply {
            addAll(modified)
        }

        val untrackedFolders = status.untrackedFolders.toMutableList<String>()
        val stagedFiles = mutableListOf<FileCommit>().apply {
            addAll(added)
            addAll(changed)
            addAll(missing)
            addAll(removed)
            addAll(conflicting)
        }

        Message.Success(obj = StatusToCommit(
            stagedFiles = stagedFiles,
            unStagedFiles = untracked,
            untrackedFolders = untrackedFolders,
        ))
    }

    fun addFileToStageArea(fileName: String): Message<Unit> = tryCatch {
        git.add().addFilepattern(fileName).call()

        Message.Success(obj = Unit)
    }

    fun removeFileToStageArea(fileName: String): Message<Unit> = tryCatch {
        git.reset().setRef(Constants.HEAD).addPath(fileName).call()

        Message.Success(obj = Unit)
    }

    fun fileDiff(filename: String): Message<Diff> = tryCatch {
        val newTreeParser = prepareTreeParserByBranch(git.repository, git.repository.fullBranch)
        val diff = git.diff().setOldTree(newTreeParser).setPathFilter(PathFilter.create(filename)).call()
        val entry = diff.first()
        val out = ByteArrayOutputStream(128)
        val formatter = DiffFormatter(out)
        formatter.setRepository(git.repository)
        formatter.format(entry)
        formatter.flush()
        val fileName = when (entry.changeType.name) {
            DiffEntry.ChangeType.ADD.name -> entry.newPath
            DiffEntry.ChangeType.COPY.name -> entry.oldPath + "->" + entry.newPath
            DiffEntry.ChangeType.DELETE.name -> entry.oldPath
            DiffEntry.ChangeType.MODIFY.name -> entry.oldPath
            DiffEntry.ChangeType.RENAME.name -> entry.oldPath + "->" + entry.newPath
            else -> entry.oldPath + "->" + entry.newPath
        }

        Message.Success(obj = Diff(
            changeType = entry.changeType,
            fileName = fileName,
            content = out.toString()
        ))
    }

    fun commitFiles(message: String): Message<Unit> = tryCatch {
        git.commit().setMessage(message).call()

        Message.Success(obj = Unit)
    }

    fun revertFile(fileName: String): Message<Unit> = tryCatch {
        val head: Ref = git.repository.exactRef(git.repository.fullBranch)
        git.checkout().setStartPoint(head.objectId.name).addPath(fileName).call();

        Message.Success(obj = Unit)
    }
}
