package br.com.source.view.model

import androidx.compose.ui.graphics.Color
import br.com.source.model.util.emptyString
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.lib.ObjectId
import java.util.*

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
    val changes: List<Change> = extractChanges()

    private fun extractChanges(): List<Change> {
        val scanner = Scanner(content)
        val changes = mutableListOf<Change>()
        var change: Change? = null
        var currentNumberNew: Int? = null
        var currentNumberOld: Int? = null

        while (scanner.hasNextLine()) {
            val line: String = scanner.nextLine()
            if(line.startsWith("@@")) {
                if(change != null) {
                    changes.add(change)
                    change = null
                }
                change = Change(positionOfChanges = extractPositionOfChanges(line))
                currentNumberOld = change.positionOfChanges.startOld
                currentNumberNew = change.positionOfChanges.startNew
            } else {
                val currentLine = extractLine(line, currentNumberOld, currentNumberNew)
                when (currentLine) {
                    is Line.Add -> {
                        currentNumberNew = currentNumberNew?.plus(1)
                    }
                    is Line.Remove -> {
                        currentNumberOld = currentNumberOld?.plus(1)
                    }
                    else -> {
                        currentNumberOld = currentNumberOld?.plus(1)
                        currentNumberNew = currentNumberNew?.plus(1)
                    }
                }
                change?.lines?.add(currentLine)
            }
        }
        if(change != null) {
            changes.add(change)
        }
        scanner.close()

        return changes
    }

    private fun extractPositionOfChanges(content: String): PositionOfChanges { //@@ -1 +0,0 @@ --> [1], [0, 0]
        val list = content.replace("@@", "").trim().replace("+", "").replace("-", "").split(" ")
        var startOld: Int = 0
        var totalOld: Int = 0
        var startNew: Int = 0
        var totalNew: Int = 0

        if(list.isNotEmpty()) {
            if(list[0].isNotEmpty()) {
                val temp = list[0].split(",")
                if(temp[0].isNotEmpty()) {
                    startOld = temp[0].toInt()
                }
                if(temp.size > 1) {
                    totalOld = list[0].split(",")[1].toInt()
                }
            }
            if(list[1].isNotEmpty()) {
                val temp = list[1].split(",")
                if(temp[0].isNotEmpty()) {
                    startNew = temp[0].toInt()
                }
                if(temp.size > 1) {
                    totalNew = temp[1].toInt()
                }
            }
        }

        return PositionOfChanges(startOld = startOld, totalOld = totalOld, startNew = startNew, totalNew = totalNew)
    }

    private fun extractLine(content: String, numberRemove: Int? = null, numberAdd: Int? = null): Line {
        return if(content.startsWith("+")) {
            Line.Add(content.removePrefix("+"), numberAdd)
        } else if(content.startsWith("-")) {
            Line.Remove(content.removePrefix("-"), numberRemove)
        } else {
            Line.Unmodified(content, numberRemove, numberAdd)
        }
    }

    override fun toString(): String {
        return "Diff(fileName='$fileName', changeType=$changeType, content='$content'"
    }
}

data class Change(
    var lines: MutableList<Line> = mutableListOf(),
    var positionOfChanges: PositionOfChanges
)

data class PositionOfChanges(
    val startOld: Int,
    val totalOld: Int,
    val startNew: Int,
    val totalNew: Int,
) {
    override fun toString(): String {
        return "PositionOfChanges(startOld=$startOld, totalOld=$totalOld, startNew=$startNew, totalNew=$totalNew)"
    }
}

open class Line(val content: String, var numberOld: Int? = null, var numberNew: Int? = null) {
    class Add(content: String, numberNew: Int? = null): Line(content, numberNew = numberNew)
    class Remove(content: String, numberOld: Int? = null): Line(content, numberOld = numberOld)
    class Unmodified(content: String, numberOld: Int? = null, numberNew: Int? = null): Line(content, numberOld, numberNew)
}

data class Commit(
    val stagedFiles: List<FileCommit>,
    val message: String
)

data class StatusToCommit(
    val stagedFiles: MutableList<FileCommit>,
    val unStagedFiles: MutableList<FileCommit>,
    val untrackedFolders: MutableList<String>,
)

data class ConflictOnCommit(
    val file: FileCommit,
)

data class FileCommit(
    val name: String,
    val changeType: DiffEntry.ChangeType,
    val isConflict: Boolean = false,
    val hash: String? = null
) {
    fun simpleName(): String {
        return name.split("/").last()
    }
}

data class CommitItem(
    val hash: String,
    val abbreviatedHash: String,
    val fullMessage: String,
    val shortMessage: String,
    val author: String,
    val date: String,
    val node: Node
) {
    fun resume(): String {
        return "Hash: $hash\nAuthor: $author\nDate: $date\nMessage: $fullMessage".trimIndent()
    }
}

//http://bit-booster.com/graph.html
//https://stackoverflow.com/a/34987899/7249382

//git log --pretty='%h|%p|%d'
//git log --all --date-order --pretty="%H|%P|%d"



data class Node(
    val hash: String,
    val color: Color = Color.Blue,
    val parents: List<String> = emptyList(),
    val line: List<String> = emptyList(),
    val branch: Branch? = null,
    val tags: List<Tag> = emptyList(),
) {
    override fun toString(): String {
        return "$hash|${parents.joinToString(separator = " ")}"
    }

    fun hasParent() = parents.isNotEmpty()
}