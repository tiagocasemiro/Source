package br.com.source.view.dashboard.right.composes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import br.com.source.view.model.Node

internal val canvasWidth: Float = 7.8f
internal val canvasHeight: Float = 25f
internal val strokeWidth = 1.9F
internal val radius = 4f
internal val insideRadius = 2.1f

@Composable
fun DrawTreeGraph(node: Node, nextNode: Node?, background: Color) {


    Canvas(modifier = Modifier.height(25.dp).width(80.dp).background(background)) {
        if(node.line.isEmpty()) {
            topCommit(node, nextNode, background)
        } else {
            if(node.hasParent()) {
                commit(node, nextNode, background)
            } else {
                bottomCommit(node, background)
            }
        }
        sequence(node, nextNode)
    }
}

fun DrawScope.sequence(node: Node, nextNode: Node?) {
    node.line.forEachIndexed { index, it ->
        val x = index * canvasWidth
        if(node.hash != it.hash && it.hash.isNotEmpty()) {
            drawLine(
                start = Offset(x = x + (canvasWidth / 2), y = 0f),
                end = Offset(x = x + (canvasWidth / 2), y = canvasHeight / 2),
                color = it.color,
                strokeWidth = strokeWidth
            )
        }
        var findNextNode = false
        nextNode?.line?.forEachIndexed { indexNextNode, nextLine ->
            if(nextLine == it && it.hash.isNotEmpty()) {
                findNextNode = true
                val xParent = indexNextNode * canvasWidth
                drawLine(
                    start = Offset(x = x + (canvasWidth / 2), y = (canvasHeight / 2)),
                    end = Offset(x = xParent + (canvasWidth / 2), y = canvasHeight),
                    color = it.color,
                    strokeWidth = strokeWidth
                )
            }
        }

        if(findNextNode.not()) {
            if(node.hash != it.hash && it.hash.isNotEmpty()) {
                drawLine(
                    start = Offset(x = x + (canvasWidth / 2), y = canvasHeight / 2),
                    end = Offset(x = x + (canvasWidth / 2), y = canvasHeight),
                    color = it.color,
                    strokeWidth = strokeWidth
                )
            }
        }
    }
}

fun DrawScope.commit(node: Node, nextNode: Node?, background: Color) {
    var position = 0
    node.line.forEachIndexed { index, it ->
        if(it.hash == node.hash) {
            position = index
        }
    }
    val x = position * canvasWidth
    drawLine(
        start = Offset(x = x + (canvasWidth / 2), y = 0f),
        end = Offset(x = x + (canvasWidth / 2), y = canvasHeight / 2),
        color = node.line[position].color,
        strokeWidth = strokeWidth
    )

    node.parents.forEach{ parent ->
        nextNode?.line?.forEachIndexed { indexNextNode, nextLine ->
            if(nextLine.hash == parent) {
                val xParent = indexNextNode * canvasWidth
                drawLine(
                    start = Offset(x = x + (canvasWidth / 2), y = (canvasHeight / 2)),
                    end = Offset(x = xParent + (canvasWidth / 2), y = canvasHeight),
                    color = nextLine.color,
                    strokeWidth = strokeWidth
                )
            }
        }
    }
    drawCircle(
        color = node.line[position].color,
        center = Offset(x = x + (canvasWidth / 2), y = canvasHeight / 2),
        radius = radius
    )
    drawCircle(
        color = background,
        center = Offset(x = x + (canvasWidth / 2), y = canvasHeight / 2),
        radius = insideRadius
    )
}

fun DrawScope.bottomCommit(node: Node, background: Color) {
    var position = 0
    var notFindLine = true
    node.line.forEachIndexed { index, it ->
        if(it.hash == node.hash && notFindLine) {
            position = index
            notFindLine = false
        }
    }
    val x = position * canvasWidth

    drawLine(
        start = Offset(x = x + (canvasWidth / 2), y = 0f),
        end = Offset(x = x + (canvasWidth / 2), y = canvasHeight / 2),
        color = node.line[position].color,
        strokeWidth = strokeWidth
    )
    drawCircle(
        color = node.line[position].color,
        center = Offset(x = x + (canvasWidth / 2), y = canvasHeight / 2),
        radius = radius
    )
    drawCircle(
        color = background,
        center = Offset(x = x + (canvasWidth / 2), y = canvasHeight / 2),
        radius = insideRadius
    )
}

fun DrawScope.topCommit(node: Node, nextNode: Node?, background: Color) {
    node.parents.forEachIndexed { indexParent, parent ->
        nextNode?.line?.forEachIndexed { indexNextNode, nextline ->
            if(nextline.hash == parent) {
                val xParent = indexNextNode * canvasWidth
                drawLine(
                    start = Offset(x = (canvasWidth / 2), y = canvasHeight / 2),
                    end = Offset(x = xParent + (canvasWidth / 2), y = canvasHeight),
                    color = nextline.color,
                    strokeWidth = strokeWidth
                )
            }
        }
    }
    drawCircle(
        color = nextNode?.line?.first()?.color?: Color.Blue,
        center = Offset(x = (canvasWidth / 2), y = canvasHeight / 2),
        radius = radius
    )
    drawCircle(
        color = background,
        center = Offset(x = (canvasWidth / 2), y = canvasHeight / 2),
        radius = insideRadius
    )
}
