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
                change = Change(positionOfChanges = extractPositionOfChanges(line), changePosition = line)
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
    var positionOfChanges: PositionOfChanges,
    var changePosition: String,
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

data class StatusToCommit(
    val stagedFiles: MutableList<FileCommit>,
    val unStagedFiles: MutableList<FileCommit>,
    val untrackedFolders: MutableList<String>,
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

data class CommitDetail(
    val filesFromCommit: List<FileCommit> = emptyList(),
    val resume: String? = null
)

data class CommitItem(
    val hash: String,
    val abbreviatedHash: String,
    val fullMessage: String,
    val shortMessage: String,
    val author: String,
    val date: String,
    val node: Node,
    var drawLine: List<Draw> = emptyList()
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
    val line: List<Item?>,
    val parents: List<String>,
    val branch: Branch? = null,
    val tags: List<Tag> = emptyList(),
) {
    override fun toString(): String {
        return hash
    }
}

sealed class Draw {
    class Line(val start: Point, val end: Point, val color: Color): Draw()
    class Commit(val index: Int, val color: Color): Draw()
}

data class Point(
    val index: Int,
    val position: Position
)

enum class Position {
    TOP, MEDDLE, BOTTOM;
}

data class Item(
    val hash: String,
    val color: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Item

        if (hash != other.hash) return false

        return true
    }

    override fun hashCode(): Int {
        return hash.hashCode()
    }

    override fun toString(): String {
        return hash
    }

    fun copy(newHash: String): Item {
        return Item(newHash, color)
    }
}

internal val paletteColorGraph = mutableListOf(
    Color(0,37,234),
    Color(36,146,47),
    Color(185,0,0),
    Color(147,147,147),
    Color(129,69,208),
    Color(208,126,1),
    Color(0xFF1B1464),
    Color(0xFFB53471),
    Color(0xFF12CBC4),
    Color(0xFF5758BB),
    Color(0xFFED4C67),
    Color(0xFF006266),
    Color(0xFFEE5A24),
    Color(0xFF383838),
    Color(0xFF40407a),
    Color(0xFF60992D),
    Color(0xFF706fd3),
    Color(0xFF218c74),
    Color(0xFFcd6133),
    Color(0xFF2c2c54),
    Color(0xFF474787),
    Color(0xFFff5252),
    Color(0xFF388E3C),
    Color(0xFFC2185B),
)
internal var indexOfColor = 0

fun clearUsedColorOfGraph() {
    indexOfColor = 0
}

fun retryColor(index: Int): Color {
    val random = Random()
    return if(index < paletteColorGraph.size)
        paletteColorGraph[index]
    else
        Color(random.nextInt(200), random.nextInt(200), random.nextInt(200))
}

fun generateColor(): Int {
    val color = if(indexOfColor < paletteColorGraph.size) {
        indexOfColor
    } else {
        randomColor()
    }
    indexOfColor += 1

    return color
}

fun randomColor(): Int {
    val random = Random()
    val color = Color(random.nextInt(200), random.nextInt(200), random.nextInt(200))
    if(paletteColorGraph.contains(color).not()) {
        paletteColorGraph.add(color)

        return paletteColorGraph.indexOfLast { true }
    }

    return randomColor()
}