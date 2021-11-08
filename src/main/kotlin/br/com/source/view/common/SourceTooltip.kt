package br.com.source.view.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.rememberCursorPositionProvider
import androidx.compose.ui.window.rememberComponentRectPositionProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


/**
 * Sets the tooltip for an element.
 *
 * @param tooltip Composable content of the tooltip.
 * @param modifier The modifier to be applied to the layout.
 * @param delayMillis Delay in milliseconds.
 * @param tooltipPlacement Defines position of the tooltip.
 * @param content Composable content that the current tooltip is set to.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SourceTooltipArea(
    tooltip: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    delayMillis: Int = 500,
    tooltipPlacement: TooltipPlacement = TooltipPlacement.CursorPoint(
        offset = DpOffset(0.dp, 16.dp)
    ),
    forceClose: MutableState<Boolean> = mutableStateOf(false),
    forceOpen: MutableState<Boolean> = mutableStateOf(false),
    content: @Composable () -> Unit
) {
    val mousePosition = remember { mutableStateOf(IntOffset.Zero) }
    var parentBounds by remember { mutableStateOf(IntRect.Zero) }
    var isVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var job: Job? by remember { mutableStateOf(null) }

    fun startShowing() {
        job?.cancel()
        job = scope.launch {
            delay(delayMillis.toLong())
            isVisible = true
        }
    }

    fun hide() {
        job?.cancel()
        isVisible = false
    }

    if(forceClose.value) {
        hide()
        forceClose.value = false
    }
    if(forceOpen.value) {
        startShowing()
        forceOpen.value = false
    }

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                val size = coordinates.size
                val position = IntOffset(
                    coordinates.positionInWindow().x.toInt(),
                    coordinates.positionInWindow().y.toInt()
                )
                parentBounds = IntRect(position, size)
            }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val position = event.changes.first().position
                        when (event.type) {
                            PointerEventType.Move -> {
                                mousePosition.value = IntOffset(
                                    position.x.toInt() + parentBounds.left,
                                    position.y.toInt() + parentBounds.top
                                )
                            }
                            PointerEventType.Enter -> {
                                startShowing()
                            }
                            PointerEventType.Exit -> {
                                hide()
                            }
                        }
                    }
                }
            }
            .pointerInput(Unit) {
                detectDown {
                    hide()
                }
            }
    ) {
        content()
        if (isVisible) {
            @OptIn(ExperimentalFoundationApi::class)
            Popup(
                popupPositionProvider = tooltipPlacement.positionProvider(),
                onDismissRequest = { isVisible = false }
            ) {
                tooltip()
            }
        }
    }
}

private suspend fun PointerInputScope.detectDown(onDown: (Offset) -> Unit) {
    while (true) {
        awaitPointerEventScope {
            val event = awaitPointerEvent(PointerEventPass.Initial)
            val down = event.changes.find { it.changedToDown() }
            if (down != null) {
                onDown(down.position)
            }
        }
    }
}

/**
 * An interface for providing a [PopupPositionProvider] for the tooltip.
 */
@ExperimentalFoundationApi
interface TooltipPlacement {
    /**
     * Returns [PopupPositionProvider] implementation.
     */
    @Composable
    fun positionProvider(): PopupPositionProvider

    /**
     * [TooltipPlacement] implementation for providing a [PopupPositionProvider] that calculates
     * the position of the popup relative to the current mouse cursor position.
     *
     * @param offset [DpOffset] to be added to the position of the popup.
     * @param alignment The alignment of the popup relative to the current cursor position.
     * @param windowMargin Defines the area within the window that limits the placement of the popup.
     */
    @ExperimentalFoundationApi
    class CursorPoint(
        private val offset: DpOffset = DpOffset.Zero,
        private val alignment: Alignment = Alignment.BottomEnd,
        private val windowMargin: Dp = 4.dp
    ) : TooltipPlacement {
        @OptIn(ExperimentalComposeUiApi::class)
        @Composable
        override fun positionProvider() = rememberCursorPositionProvider(
            offset,
            alignment,
            windowMargin
        )
    }

    /**
     * [TooltipPlacement] implementation for providing a [PopupPositionProvider] that calculates
     * the position of the popup relative to the current component bounds.
     *
     * @param anchor The anchor point relative to the current component bounds.
     * @param alignment The alignment of the popup relative to the [anchor] point.
     * @param offset [DpOffset] to be added to the position of the popup.
     */
    @ExperimentalFoundationApi
    class ComponentRect(
        private val anchor: Alignment = Alignment.BottomCenter,
        private val alignment: Alignment = Alignment.BottomCenter,
        private val offset: DpOffset = DpOffset.Zero
    ) : TooltipPlacement {
        @OptIn(ExperimentalComposeUiApi::class)
        @Composable
        override fun positionProvider() = rememberComponentRectPositionProvider(
            anchor,
            alignment,
            offset
        )
    }
}
