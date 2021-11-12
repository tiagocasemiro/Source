package br.com.source.view.dashboard.right.composes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import br.com.source.model.domain.LocalRepository
import br.com.source.view.common.SourceHorizontalSplitter
import br.com.source.view.common.SourceVerticalSplitter
import br.com.source.view.common.StatusStyle
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.VerticalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState

@Composable
fun CommitCompose(localRepository: LocalRepository, close: () -> Unit, leftContainerReload: () -> Unit) {
    val hSplitterStateOne = rememberSplitPaneState(0.75f)
    val hSplitterStateTwo = rememberSplitPaneState(0.5f)
    val vSplitterStateOne = rememberSplitPaneState(0.5f)

    VerticalSplitPane(
        splitPaneState = hSplitterStateOne,
        modifier = Modifier.background(StatusStyle.backgroundColor)
    ) {
        first {
           HorizontalSplitPane(
               splitPaneState = vSplitterStateOne,
               modifier = Modifier.background(StatusStyle.backgroundColor)
           ) {
               first {
                   VerticalSplitPane(
                       splitPaneState = hSplitterStateTwo,
                       modifier = Modifier.background(StatusStyle.backgroundColor)
                   ) {
                       first {
                           StagedFilesCompose(localRepository)
                       }
                       second {
                           UnstagedFilesCompose(localRepository)
                       }
                       SourceVerticalSplitter()
                   }
               }
               second {
                   DiffFileCompose(localRepository)
               }
               SourceHorizontalSplitter()
           }
        }
        second{
            MessageContainer(localRepository)
        }
        SourceVerticalSplitter()
    }
}


@Composable
internal fun StagedFilesCompose(localRepository: LocalRepository) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("staged files")
    }
}

@Composable
internal fun UnstagedFilesCompose(localRepository: LocalRepository) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("unstaged files")
    }
}

@Composable
internal fun DiffFileCompose(localRepository: LocalRepository) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("diff file")
    }
}

@Composable
fun MessageContainer(localRepository: LocalRepository) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("message")
    }
}