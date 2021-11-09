package br.com.source.view.dashboard.right.composes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import br.com.source.model.domain.LocalRepository
import br.com.source.view.common.SourceVerticalSplitter
import br.com.source.view.common.StatusStyle
import org.jetbrains.compose.splitpane.VerticalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState

@Composable
fun CommitCompose(localRepository: LocalRepository, close: () -> Unit, leftContainerReload: MutableState<Boolean>) {
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

}

@Composable
fun BottonContainer(localRepository: LocalRepository) {
    Box(Modifier.fillMaxSize())
}