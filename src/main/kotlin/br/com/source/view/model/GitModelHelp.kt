package br.com.source.view.model

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
        while (scanner.hasNextLine()) {
            val line: String = scanner.nextLine()
            if(line.contains("@@")) {
                if(change != null) {
                    changes.add(change)
                }
                change = Change(positionOfChanges = extractPositionOfChanges(line))
            } else {
                change?.lines?.add(extractLine(line))
            }
        }
        scanner.close()

        return changes
    }

    private fun extractPositionOfChanges(content: String): PositionOfChanges { //@@ -13,3 +13,18 @@
        val list = content.replace("@@", "").trim().replace("+", "").replace("-", "").split(" ")
        val startRemove: Int = list[0].split(",")[0].toInt()
        val totalRemove: Int = list[0].split(",")[1].toInt()
        val startAdd: Int = list[1].split(",")[0].toInt()
        val totalAdd: Int = list[1].split(",")[1].toInt()
        return PositionOfChanges(
            startRemove = startRemove,
            totalRemove = totalRemove,
            startAdd = startAdd,
            totalAdd = totalAdd
        )
    }

    private fun extractLine(content: String): Line {
        return if(content.startsWith("+")) {
            Line.Add(content.removePrefix("+"))
        } else if(content.startsWith("-")) {
            Line.Remove(content.removePrefix("-"))
        } else {
            Line.Unmodified(content)
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
    val startRemove: Int,
    val totalRemove: Int,
    val startAdd: Int,
    val totalAdd: Int,
) {
    override fun toString(): String {
        return "PositionOfChanges(startRemove=$startRemove, totalRemove=$totalRemove, startAdd=$startAdd, totalAdd=$totalAdd)"
    }
}

open class Line(val content: String) {
    class Add(content: String): Line(content)
    class Remove(content: String): Line(content)
    class Unmodified(content: String): Line(content)
}

