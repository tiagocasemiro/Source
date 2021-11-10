package br.com.source.view.model

import br.com.source.model.util.emptyString
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.lib.ObjectId

data class Branch(
    val isCurrent: Boolean = false,
    val fullName: String,
) {
    val clearName = fullName.replaceFirst("refs/heads/", emptyString())
        .replaceFirst("refs/remotes/origin/", emptyString())
    val name: String = fullName.split("/").last()
    val folder: String = clearName
        .split("/")
        .toMutableList()
        .apply { removeLast() }.toString()
        .replace("[", emptyString())
        .replace("]", emptyString())

    fun hasFolder(): Boolean {
        return clearName.contains("/")
    }

    override fun toString(): String {
        return "Branch(isCurrent=$isCurrent, fullName='$fullName', name='$name', folder='$folder')"
    }
}

data class Tag(
    val name: String,
    val objectId: ObjectId
)

data class Stash(
    val originalName: String,
    val shortMessage: String,
    val index: Int,
    val objectId: String,
    val parentObjectId: String
) {
    val name: String = shortMessage.split(":").takeIf {it.size > 1}?.get(1)?.trimStart()?.trimEnd()
        ?.split(" ").takeIf { it != null && it.size > 1}?.toMutableList().apply {this?.removeFirst()}
        ?.joinToString(separator = " ")?: "stash@{$index}"
}

data class Diff(
    val fileName: String,
    val changeType: DiffEntry.ChangeType,
    val content: String
) {
    override fun toString(): String {
        return "Diff(fileName='$fileName', changeType=$changeType, content='')"
    }
}