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
import br.com.source.model.util.emptyString
import br.com.source.view.model.*

internal val canvasWidth: Float = 7.8f
internal val canvasHeight: Float = 25f
internal val strokeWidth = 1.9F
internal val radius = 4f
internal val insideRadius = 2.1f

@Composable
fun DrawTreeGraph(line: List<Draw>, background: Color) {
    Canvas(modifier = Modifier.height(25.dp).width(200.dp).background(background)) {
        line.forEach { draw ->
            when(draw) {
                is Draw.Line -> line(draw.start, draw.end, draw.color)
                is Draw.Commit -> commit(draw.index, draw.color, background)
            }
        }
    }
}

fun DrawScope.line(start: Point, end: Point, color: Color) {
    val xStart = start.index * canvasWidth
    val xEnd = end.index * canvasWidth
    val yStart = when(start.position) {
        Position.TOP -> 0f
        Position.MEDDLE -> canvasHeight / 2
        Position.BOTTOM -> canvasHeight
    }
    val yEnd = when(end.position) {
        Position.TOP -> 0f
        Position.MEDDLE -> canvasHeight / 2
        Position.BOTTOM -> canvasHeight
    }
    drawLine(
        start = Offset(x = xStart + (canvasWidth / 2), y = yStart),
        end = Offset(x = xEnd + (canvasWidth / 2), y = yEnd),
        color = color,
        strokeWidth = strokeWidth
    )
}

fun DrawScope.commit(index: Int, color: Color, background: Color) {
    val x = index * canvasWidth
    drawCircle(
        color = color,
        center = Offset(x = x + (canvasWidth / 2), y = canvasHeight / 2),
        radius = radius
    )
    drawCircle(
        color = background,
        center = Offset(x = x + (canvasWidth / 2), y = canvasHeight / 2),
        radius = insideRadius
    )
}

fun processLog(commits: List<CommitItem>): List<List<Draw>> { //todo  posso receber o before , current e after line para evitat um loop : (esse loop) ->  val drawTree = processLog(commits.value)
   println("----------------------------------------------------")
    commits.forEachIndexed { index, commit ->
        println("" + index + " - " + commit.node.hash + " | " + commit.node.line + " " + commit.node.parents)
    }
    println("----------------------------------------------------")




    val graph = mutableListOf<List<Draw>>()
    val color = Color.Blue
    val red = Color.Red
    val extraColumn = mutableListOf<Int>()
    commits.forEachIndexed { index, commit ->
        val currentNode = commit.node
        val nextNode = if(index + 1 < commits.size) commits[index + 1].node else null
        val beforeNode = if(index > 0) commits[index - 1].node else null
        var lineGraph = mutableListOf<Draw>()

        if(index == 0) {
            commits[0].node.parents.forEachIndexed {  indexCurrentItemLine, _ ->
                lineGraph.add(Draw.Line(start = Point(0, Position.MEDDLE), end = Point(indexCurrentItemLine, Position.BOTTOM), color = color))
            }
            lineGraph.add(Draw.Commit(0, color))
            graph.add(lineGraph)

            return@forEachIndexed
        }

        lineGraph = mutableListOf()
        var isCurrentCommitNotDrawn = true
        currentNode.line.forEachIndexed { indexCurrentItemLine, currentItemLine -> // varrer a linha item a item
            if(currentItemLine == currentNode.hash && isCurrentCommitNotDrawn) { // se for commit achar os filhos e colocar o commit
                    currentNode.parents.forEach{ parent ->
                        nextNode?.line?.forEachIndexed { indexNextItemLine, nextItemLine ->
                            if (nextItemLine == parent && currentItemLine.isNotEmpty() ) { // TODO um hash pode estar em 2 branches
                                lineGraph.add(Draw.Line(start = Point(indexCurrentItemLine, Position.MEDDLE), end = Point(indexNextItemLine, Position.BOTTOM), color))
                                if(nextItemLine == nextNode.hash) {
                                    return@forEach
                                }
                            }
                        }
                    }

                if(beforeNode != null && beforeNode.line.isNotEmpty() && beforeNode.line.size > indexCurrentItemLine) {
                    beforeNode.line.forEachIndexed { indexBeforeItemLine, beforeItemLine ->
                        if(beforeItemLine == currentItemLine) {
                            lineGraph.add(Draw.Line(start = Point(indexBeforeItemLine, Position.TOP), end = Point(indexCurrentItemLine, Position.MEDDLE), color))
                        }
                    }
                    if(currentItemLine == "e1b9781") {
                        println()
                    }

                    if(beforeNode.parents.contains(currentItemLine)) {
                        lineGraph.add(Draw.Line(start = Point(indexCurrentItemLine, Position.TOP), end = Point(indexCurrentItemLine, Position.MEDDLE), color))
                    }
                }


                if(index == 1 || currentNode.parents.isEmpty()) {
                    lineGraph.add(Draw.Line(start = Point(indexCurrentItemLine, Position.TOP), end = Point(indexCurrentItemLine, Position.MEDDLE), color))
                }

                if(isCurrentCommitNotDrawn) {
                    lineGraph.add(Draw.Commit(indexCurrentItemLine, color))
                    isCurrentCommitNotDrawn = false
                }
            } else {
                run breakNextItemLine@{
                    nextNode?.line?.forEachIndexed { indexNextItemLine, nextItemLine ->
                        if (nextItemLine == currentItemLine && currentItemLine.isNotEmpty()) {
                            lineGraph.add(Draw.Line(start = Point(indexCurrentItemLine, Position.TOP), end = Point(indexCurrentItemLine, Position.BOTTOM), color))
                            // lineGraph.add(Draw.Line(start = Point(indexCurrentItemLine, Position.TOP), end = Point(indexNextItemLine, Position.BOTTOM), color))
                            return@breakNextItemLine
                        }
                    }
                }
            }
        }

        isCurrentCommitNotDrawn = true
        graph.add(lineGraph)
    }

    return graph
}