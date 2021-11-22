package br.com.source.view.dashboard.right.composes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import br.com.source.view.model.Node

internal val canvasWidth: Float = 5f
internal val canvasHeight: Float = 25f
internal val strokeWidth = 1.5F
internal val radius = 2.5f

@Composable
fun DrawTreeGraph(node: Node, nextNode: Node?) {
    Canvas(modifier = Modifier.height(25.dp).width(80.dp)) {
        if(node.line.isEmpty()) {
            topCommit(node, nextNode)
        } else {
            if(node.hasParent()) {
                commit(node, nextNode)
            } else {
                bottomCommit(node)
            }
        }
        sequence(node, nextNode)
    }
}

fun DrawScope.sequence(node: Node, nextNode: Node?) {
    node.line.forEachIndexed { index, it ->
        val x = index * canvasWidth
        if(node.hash != it && it.isNotEmpty()) {
            drawLine(
                start = Offset(x = x + (canvasWidth / 2), y = 0f),
                end = Offset(x = x + (canvasWidth / 2), y = canvasHeight / 2),
                color = Color.Blue,
                strokeWidth = strokeWidth
            )
        }
        var findNextNode = false
        nextNode?.line?.forEachIndexed { indexNextNode, nextNode ->
            if(nextNode == it && it.isNotEmpty()) {
                findNextNode = true
                val xParent = indexNextNode * canvasWidth
                drawLine(
                    start = Offset(x = x + (canvasWidth / 2), y = (canvasHeight / 2)),
                    end = Offset(x = xParent + (canvasWidth / 2), y = canvasHeight),
                    color = Color.Blue,
                    strokeWidth = strokeWidth
                )
            }
        }

        if(findNextNode.not()) {
            if(node.hash != it && it.isNotEmpty()) {
                drawLine(
                    start = Offset(x = x + (canvasWidth / 2), y = canvasHeight / 2),
                    end = Offset(x = x + (canvasWidth / 2), y = canvasHeight),
                    color = Color.Blue,
                    strokeWidth = strokeWidth
                )
            }
        }
    }
}

fun DrawScope.commit(node: Node, nextNode: Node?) {
    var position = 0
    node.line.forEachIndexed { index, it ->
        if(it == node.hash) {
            position = index
        }
    }
    val x = position * canvasWidth
    drawCircle(
        color = Color.Blue,
        center = Offset(x = x + (canvasWidth / 2), y = canvasHeight / 2),
        radius = radius
    )
    drawLine(
        start = Offset(x = x + (canvasWidth / 2), y = 0f),
        end = Offset(x = x + (canvasWidth / 2), y = canvasHeight / 2),
        color = Color.Blue,
        strokeWidth = strokeWidth
    )

    node.parents.forEach{ parent ->
        nextNode?.line?.forEachIndexed { indexNextNode, nextNode ->
            if(nextNode == parent) {
                val xParent = indexNextNode * canvasWidth
                drawLine(
                    start = Offset(x = x + (canvasWidth / 2), y = (canvasHeight / 2)),
                    end = Offset(x = xParent + (canvasWidth / 2), y = canvasHeight),
                    color = Color.Blue,
                    strokeWidth = strokeWidth
                )
            }
        }
    }
}

fun DrawScope.bottomCommit(node: Node) {
    var position = 0
    var notFindLine = true
    node.line.forEachIndexed { index, it ->
        if(it == node.hash && notFindLine) {
            position = index
            notFindLine = false
        }
    }
    val x = position * canvasWidth

    drawLine(
        start = Offset(x = x + (canvasWidth / 2), y = 0f),
        end = Offset(x = x + (canvasWidth / 2), y = canvasHeight / 2),
        color = Color.Blue,
        strokeWidth = strokeWidth
    )
    drawCircle(
        color = Color.Blue,
        center = Offset(x = x + (canvasWidth / 2), y = canvasHeight / 2),
        radius = radius
    )
}

fun DrawScope.topCommit(node: Node, nextNode: Node?) {
    var position = 0
    node.line.forEachIndexed { index, it ->
        if(it == node.hash) {
            position = index
            return@forEachIndexed
        }
    }
    val x = position * canvasWidth
    drawCircle(
        color = Color.Blue,
        center = Offset(x = x + (canvasWidth / 2), y = canvasHeight / 2),
        radius = radius
    )
    node.parents.forEachIndexed { indexParent, parent ->
        nextNode?.line?.forEachIndexed { indexNextNode, nextline ->
            if(nextline == parent) {
                val xParent = indexNextNode * canvasWidth
                drawLine(
                    start = Offset(x = x + (canvasWidth / 2), y = canvasHeight / 2),
                    end = Offset(x = xParent + (canvasWidth / 2), y = canvasHeight),
                    color = Color.Blue,
                    strokeWidth = strokeWidth
                )
            }
        }
    }
}
