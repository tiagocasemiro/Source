package br.com.source.view.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.source.model.domain.LocalRepository
import br.com.source.view.common.SourceHorizontalSplitter
import br.com.source.view.common.StatusStyle.backgroundColor
import br.com.source.view.common.itemRepositoryBackground
import br.com.source.view.dashboard.left.LeftContainer
import br.com.source.view.dashboard.logo.LogoContainer
import br.com.source.view.dashboard.right.RightContainer
import br.com.source.view.dashboard.right.RightState
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState

@Composable
fun Dashboard(localRepository: LocalRepository, close: () -> Unit) {
    val vSplitterState = rememberSplitPaneState(0.25f)
    val leftContainerReload = remember { mutableStateOf(false) }
    val rightState = remember { mutableStateOf<RightState>(RightState.History) }

    HorizontalSplitPane(
        splitPaneState = vSplitterState,
        modifier = Modifier.background(backgroundColor)
    ) {
        first {
            Column {
                Box(Modifier.fillMaxWidth().height(80.dp)) {
                    LogoContainer()
                }
                Spacer(modifier = Modifier.background(itemRepositoryBackground).height(1.dp).fillMaxWidth())
                LeftContainer(localRepository, leftContainerReload = leftContainerReload,
                    openStash =  {
                        rightState.value = RightState.OpenStash(it)
                    },
                    history = {
                        rightState.value = RightState.History
                    }
                )
            }
        }
        second {
            RightContainer(localRepository, rightState, close, leftContainerReload = {
                leftContainerReload.value = true
            })
        }
        SourceHorizontalSplitter()
    }
}
