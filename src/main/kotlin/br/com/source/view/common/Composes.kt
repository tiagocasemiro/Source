package br.com.source.view.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.PointerIconDefaults
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.SplitPaneScope
import java.awt.Cursor
import java.io.File
import javax.swing.JFileChooser
import javax.swing.JPanel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SourceChooserFolderButton(onClick: () -> Unit) {
    Image(
        painter = painterResource("images/folder-icon.svg"),
        contentDescription = "Button select directory of repository",
        modifier = Modifier
            .clickable {
                onClick()
            }
            .pointerHoverIcon(PointerIconDefaults.Hand)
            .background(Color.Transparent)
            .size(13.dp, 11.dp)
    )
}

@Composable
fun SourceChooseFolderDialog(pathRemember: MutableState<String>) {
    SwingPanel(
        background = Color.Transparent,
        modifier = Modifier.size(0.dp, 0.dp),
        factory = {
            JPanel()
        },
        update = { pane ->
            val chooser = JFileChooser()
            chooser.currentDirectory = File(pathRemember.value.ifEmpty { System.getProperty("user.home") })
            chooser.dialogTitle = "Select root directory of repository"
            chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            val returnVal = chooser.showOpenDialog(pane)
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                val file = chooser.selectedFile
                pathRemember.value = file.absolutePath
            }
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.cursorForHorizontalResize(): Modifier =
    pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.cursorForVerticalResize(): Modifier =
    pointerHoverIcon(PointerIcon(Cursor(Cursor.S_RESIZE_CURSOR)))


@OptIn(ExperimentalSplitPaneApi::class)
fun SplitPaneScope.SourceHorizontalSplitter() = splitter {
    visiblePart {
        Box(
            Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(itemRepositoryBackground)
        )
    }
    handle {
        Box(
            Modifier
                .markAsHandle()
                .cursorForHorizontalResize()
                .background(SolidColor(Color.Transparent), alpha = 0.50f)
                .width(10.dp)
                .fillMaxHeight()
        )
    }
}

@OptIn(ExperimentalSplitPaneApi::class)
fun SplitPaneScope.SourceVerticalSplitter() = splitter {
    visiblePart {
        Box(
            Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(itemRepositoryBackground)
        )
    }
    handle {
        Box(
            Modifier
                .markAsHandle()
                .cursorForVerticalResize()
                .background(SolidColor(Color.Transparent), alpha = 0.50f)
                .height(10.dp)
                .fillMaxWidth()
        )
    }
}

private val loadState = mutableStateOf(false)

fun showLoad() {
    loadState.value = true
}

fun hideLoad() {
    loadState.value = false
}

@Composable
fun Load( content: @Composable BoxScope.() -> Unit) {
    Box(Modifier.fillMaxSize()) {
       content()
       if(loadState.value) {
           Box(
               Modifier.background(Color(0,0,0, 50)).fillMaxSize().pointerInput(Unit) {
                   detectTapGestures()
               },
               contentAlignment = Alignment.Center,
           ) {
               CircularProgressIndicator(
                   Modifier.background(
                       Color(255,255,255, 200),
                       RoundedCornerShape(8.dp)
                   ).padding(8.dp)
               )
           }
        }
    }
}