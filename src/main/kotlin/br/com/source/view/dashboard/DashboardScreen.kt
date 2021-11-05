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
import br.com.source.view.common.SourceVerticalSplitter
import br.com.source.view.common.StatusStyle.backgroundColor
import br.com.source.view.common.itemRepositoryBackground
import br.com.source.view.dashboard.botton.BottonContainer
import br.com.source.view.dashboard.top.TopContainer
import br.com.source.view.dashboard.left.LeftContainer
import br.com.source.view.dashboard.logo.LogoContainer
import br.com.source.view.dashboard.center.CenterContainer
import br.com.source.view.model.Stash
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.VerticalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState

@Composable
fun dashboardRepository(localRepository: LocalRepository, close: () -> Unit) {
    val vSplitterState = rememberSplitPaneState(0.25f)
    val hSplitterState = rememberSplitPaneState(0.65f)
    val centerState = remember { mutableStateOf<CenterState>(CenterState.Log) }

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
                LeftContainer(localRepository) {
                    centerState.value = CenterState.OpenStash(it)
                }
            }
        }
        second {
            VerticalSplitPane(
                splitPaneState = hSplitterState,
                modifier = Modifier.background(backgroundColor)
            ) {
                first {
                    Column {
                        Box(Modifier.fillMaxWidth().height(80.dp)) {
                            TopContainer(localRepository, close)
                        }
                        Spacer(modifier = Modifier.background(itemRepositoryBackground).height(1.dp).fillMaxWidth())
                        CenterContainer(localRepository, centerState)
                    }
                }
                second{
                    BottonContainer(localRepository)
                }
                SourceVerticalSplitter()
            }
        }
        SourceHorizontalSplitter()
    }
}

sealed class CenterState {
    object Log : CenterState()
    class OpenStash(stash: Stash): CenterState()
}