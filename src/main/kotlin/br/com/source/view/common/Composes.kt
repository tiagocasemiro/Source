package br.com.source.view.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.PointerIconDefaults
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import java.awt.Cursor
import java.awt.Cursor.getPredefinedCursor
import java.io.File
import javax.swing.BoxLayout
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


