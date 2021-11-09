package br.com.source.view.dashboard.right.composes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import br.com.source.model.domain.LocalRepository
import br.com.source.view.common.SourceVerticalSplitter
import br.com.source.view.common.StatusStyle
import org.jetbrains.compose.splitpane.VerticalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState

@Composable
fun CommitCompose(localRepository: LocalRepository, close: () -> Unit, leftContainerReload: () -> Unit) {
    val hSplitterState = rememberSplitPaneState(0.65f)

    VerticalSplitPane(
        splitPaneState = hSplitterState,
        modifier = Modifier.background(StatusStyle.backgroundColor)
    ) {
        first {
           CenterContainer(localRepository)
        }
        second{
            BottonContainer(localRepository)
        }
        SourceVerticalSplitter()
    }
}


@Composable
internal fun CenterContainer(localRepository: LocalRepository) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Commit center")
    }
}

@Composable
fun BottonContainer(localRepository: LocalRepository) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Commit bottom")
    }
}