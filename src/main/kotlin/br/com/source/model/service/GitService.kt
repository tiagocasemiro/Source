package br.com.source.model.service

import br.com.source.model.domain.CredentialType
import br.com.source.model.domain.LocalRepository
import br.com.source.model.domain.RemoteRepository
import br.com.source.model.util.*
import br.com.source.view.model.*
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import org.eclipse.jgit.api.*
import org.eclipse.jgit.api.ListBranchCommand.ListMode.REMOTE
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.lib.*
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevTree
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.transport.*
import org.eclipse.jgit.treewalk.AbstractTreeIterator
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.treewalk.filter.PathFilter
import org.eclipse.jgit.util.FS
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

internal open class Credential {
    data class Http(var username: String = emptyString(), var password: String = emptyString()): Credential()
    data class Ssh(var pathKey: String = emptyString(), var passwordKey: String = emptyString(), var host: String = emptyString()): Credential()
}



class GitService(localRepository: LocalRepository) {
    private val credential =
        if(localRepository.credentialType == CredentialType.HTTP.value)
            Credential.Http(localRepository.username, localRepository.password)
        else
            Credential.Ssh(localRepository.pathKey, localRepository.passwordKey, localRepository.host)
    private val git: Git = Git.open(localRepository.fileWorkDir())

    fun localBranches(): Message<List<Branch>> = tryCatch {
        val refs = git.branchList().call()
        Message.Success(obj = refs.filter {
            it.name != "refs/heads/HEAD" && it.name.endsWith("/HEAD").not() && it.name != "HEAD"
        }.map {
            Branch(fullName = it.name, isCurrent = it.name == git.repository.fullBranch)
        })
    }

    fun remoteBranches(): Message<List<Branch>> = tryCatch  {
        val refs = git.branchList().setListMode(REMOTE).call()

        Message.Success(obj = refs.filter {
           it.name != "refs/remotes/origin/HEAD" && it.name.endsWith("/HEAD").not() && it.name != "HEAD"
        }.map {
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
        if("refs/heads/$name" == git.repository.fullBranch) {
            return@tryCatch Message.Warn("Can not delete this branch.\nThis is current branch.")
        }
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
        if("refs/heads/$name" == git.repository.fullBranch) {
            return@tryCatch Message.Warn("Can not switch to this branch.\nThis is current branch.")
        }
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
        val push = git.push()
        if(credential is Credential.Ssh) {
            val sshSessionFactory: SshSessionFactory = object : JschConfigSessionFactory() {
                override fun configure(host: OpenSshConfig.Host?, session: Session?) {}

                override fun createDefaultJSch(fs: FS?): JSch {
                    val defaultJSch: JSch = super.createDefaultJSch(fs)
                    defaultJSch.addIdentity(credential.pathKey, credential.passwordKey)

                    return defaultJSch
                }
            }
            val transport = TransportConfigCallback { transport ->
                val sshTransport = transport as SshTransport
                sshTransport.sshSessionFactory = sshSessionFactory
            }
            push.setTransportConfigCallback(transport)
            if(credential.host.isNotEmpty()) {
                push.remote = credential.host
            }
        }
        if(credential is Credential.Http) {
            push.setCredentialsProvider(UsernamePasswordCredentialsProvider(credential.username, credential.password))
        }
        push.call()

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

    private fun getParentId(id: String): String? {
        val lastCommitId: ObjectId = git.repository.resolve(id)
        val revWalk = RevWalk(git.repository)
        val commit = revWalk.parseCommit(lastCommitId)

        return if(commit.parentCount > 0) {
            commit.getParent(0).name
        } else {
            null
        }
    }

    private fun getAllParentsId(id: String): List<String> {
        val lastCommitId: ObjectId = git.repository.resolve(id)
        val revWalk = RevWalk(git.repository)
        val commit = revWalk.parseCommit(lastCommitId)

        return if(commit.parentCount > 0) {
            commit.parents.map {
                it.abbreviate(7).name()
            }
        } else {
            emptyList()
        }
    }

    @Throws(IOException::class)
    private fun prepareTreeParserByObjectId(repository: Repository, objectId: String?): AbstractTreeIterator? {
        if(objectId != null) {
            RevWalk(repository).use { walk ->
                val commit: RevCommit = walk.parseCommit(ObjectId.fromString(objectId))
                val tree: RevTree = walk.parseTree(commit.tree.id)
                val treeParser = CanonicalTreeParser()
                repository.newObjectReader().use { reader -> treeParser.reset(reader, tree.id) }
                walk.dispose()

                return treeParser
            }
        }

        return null
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

    private fun createBranch(branchName: String, currentBranchName: String): Branch {
        val fullName = if(branchName.contains("HEAD")) {
            if(branchName.contains("/")) {
                branchName.replace("HEAD", currentBranchName.split("/").last())
            } else {
                currentBranchName
            }
        } else {
            branchName
        }
        val isCurrent = currentBranchName == fullName

        return Branch(isCurrent, fullName)
    }

    private fun retryBranches(commit: RevCommit, branchesAdded : MutableSet<String>): List<Branch> {
        val branches = mutableSetOf<Branch>()
        git.repository.refDatabase.refs.filter {
            RevWalk(git.repository).parseTree(it.objectId).id == commit.tree.id && it.name.contains("refs/tags").not()
        }.map {
            it.name
        }.forEach { branchName ->
            val branch = createBranch(branchName, git.repository.fullBranch)
            if(branchesAdded.add(branch.fullName)) {
                branches.add(branch)
            }
        }

        return branches.toList()
    }

    private fun retryTags(commit: RevCommit): List<Tag> {
        val refs: List<Ref> =  git.tagList().call()

        return refs.filter {
            it.objectId.name == commit.toObjectId().name
        }.map {
            Tag(it.name.split("/").last(), commit.toObjectId())
        }
    }

    @Synchronized
    fun history(): Message<List<CommitItem>> = tryCatch {
        clearUsedColorOfGraph()
        val logs = git.log().all().call()
        val currentLine = mutableListOf<Item?>()
        var parents: List<String>
        var hash: String
        val gitDateTimeFormatString = "yyyy MMM dd EEE HH:mm:ss"
        val branchesAdded = mutableSetOf<String>()
        val stashs = git.stashList().call()
        val commits = logs.filter {
            stashs.contains(it).not()
        }.mapIndexed { index,  commit ->
            val justTheAuthorNoTime = commit.authorIdent.toExternalString().split(">").toTypedArray()[0] + ">"
            val commitInstant = Instant.ofEpochSecond(commit.commitTime.toLong())
            val zoneId = commit.authorIdent.timeZone.toZoneId()
            val authorDateTime = ZonedDateTime.ofInstant(commitInstant, zoneId)
            val formattedDate = authorDateTime.format(DateTimeFormatter.ofPattern(gitDateTimeFormatString))
            parents = getAllParentsId(commit.toObjectId().name)
            hash = commit.toObjectId().abbreviate(7).name()
            val beforeLine =  currentLine.clone()
            val finalCommit = CommitItem(
                hash = commit.name,
                abbreviatedHash = hash,
                fullMessage = commit.fullMessage,
                shortMessage = commit.shortMessage,
                author = justTheAuthorNoTime,
                date = formattedDate,
                node = Node(
                    hash = hash,
                    parents = parents,
                    line = beforeLine,
                    branches = retryBranches(commit, branchesAdded),
                    tags = retryTags(commit)
                )
            )

            // find the first hash of commit on current line
            val indexHashCommit: Int? = currentLine.indexOfFirstOrNull { it?.hash == hash }

            // Add commit to last position
            if(indexHashCommit == null && index > 0) {
                beforeLine.add(Item(hash, generateColor()))
            }

            // replace the current hash with the first parent and add the other parents
            // if the before line is empty, just add the parents
            parents.forEachIndexed { i, it ->
                val containsItem = currentLine.contains(Item(it)).not()
                if(i == 0 && indexHashCommit != null && currentLine.isNotEmpty()) {
                    if(containsItem) {
                        currentLine[indexHashCommit] = currentLine[indexHashCommit]?.copy(it)
                    }
                    currentLine.removeAll { it?.hash == hash }
                } else {
                    if(containsItem) {
                        currentLine.add(Item(it, generateColor()))
                    }
                }
            }

            finalCommit
        }

        Message.Success(obj = commits)
    }

    fun filesChangesOn(objectId: String): Message<List<FileCommit>> = tryCatch {
        val newTreeParser = prepareTreeParserByObjectId(git.repository, objectId)
        val oldTreeParser = prepareTreeParserByObjectId(git.repository,  getParentId(objectId))
        val diff = git.diff().setNewTree(newTreeParser).setOldTree(oldTreeParser).call()
        val filesOnCommit = mutableListOf<FileCommit>()
        for (entry in diff) {
            val out = ByteArrayOutputStream(128)
            val formatter = DiffFormatter(out)
            formatter.setRepository(git.repository)
            formatter.format(entry)
            formatter.flush()
            if(oldTreeParser != null || (entry.changeType.name == DiffEntry.ChangeType.ADD.name)) {
                val fileName = when (entry.changeType.name) {
                    DiffEntry.ChangeType.ADD.name -> entry.newPath
                    DiffEntry.ChangeType.COPY.name -> entry.oldPath + "->" + entry.newPath
                    DiffEntry.ChangeType.DELETE.name -> entry.oldPath
                    DiffEntry.ChangeType.MODIFY.name -> entry.oldPath
                    DiffEntry.ChangeType.RENAME.name -> entry.oldPath + "->" + entry.newPath
                    else -> entry.oldPath + "->" + entry.newPath
                }
                filesOnCommit.add(FileCommit(
                    changeType = entry.changeType,
                    name = fileName,
                    hash = objectId
                ))
            }
        }

        Message.Success(obj = filesOnCommit)
    }

    fun fileDiffOn(objectId: String, filename: String): Message<Diff> = tryCatch {
        val newTreeParser = prepareTreeParserByObjectId(git.repository, objectId)
        val oldTreeParser = prepareTreeParserByObjectId(git.repository,  getParentId(objectId))
        val diff = git.diff().setNewTree(newTreeParser).setOldTree(oldTreeParser).setPathFilter(PathFilter.create(filename)).call()
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
}

class GitCloneService {
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
                    //print("$completed-") todo update the interface
                }

                override fun endTask() {
                    //println("Done")
                }

                override fun isCancelled(): Boolean {
                    return false
                }
            })
            .call()

        Message.Success(obj = Unit)
    }
}
