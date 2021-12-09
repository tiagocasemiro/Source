package br.com.source.view.dashboard.right.composes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import br.com.source.view.common.StatusStyle
import br.com.source.view.common.lineItemBackground
import br.com.source.view.common.selectedLineItemBackground
import br.com.source.view.model.*

internal val canvasWidth: Float = 7.8f
internal val canvasHeight: Float = 25f
internal val strokeWidth = 1.9F
internal val radius = 4f
internal val insideRadius = 2.1f

@Composable
fun DrawTreeGraph(line: List<Draw>, index: Int, selectedIndex: MutableState<Int>) {
    Canvas(modifier = Modifier.height(25.dp).fillMaxWidth().background(if(index == selectedIndex.value) selectedLineItemBackground else if(index % 2 == 0) StatusStyle.backgroundColor else lineItemBackground)) {
        line.forEach { draw ->
            when(draw) {
                is Draw.Line -> line(draw.start, draw.end, draw.color)
                is Draw.Commit -> commit(draw.index, draw.color, if(index == selectedIndex.value) selectedLineItemBackground else if(index % 2 == 0) StatusStyle.backgroundColor else lineItemBackground)
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
