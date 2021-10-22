package br.com.source.view.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import java.awt.Cursor
import java.awt.Cursor.getPredefinedCursor
import javax.swing.BoxLayout
import javax.swing.JPanel

@Composable
fun ChooseFolderButton(onClick: () -> Unit) {
    SwingPanel(
        modifier = Modifier.background(Color.Transparent).size(13.dp, 11.dp),
        factory = {
            JPanel().apply {
                this.layout = BoxLayout(this, BoxLayout.Y_AXIS)
                this.add(ComposePanel().apply {
                    this.cursor = getPredefinedCursor(Cursor.HAND_CURSOR)
                    setContent {
                        Image(
                            painter = painterResource("images/folder-icon.svg"),
                            contentDescription = "Button select directory of repository",
                            modifier = Modifier.fillMaxSize().clickable {
                                onClick()
                            }
                        )
                    }
                })
            }
        },
    )
}

