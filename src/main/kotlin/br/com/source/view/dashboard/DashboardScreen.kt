package br.com.source.view.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.source.model.domain.LocalRepository
import br.com.source.view.common.SourceHorizontalSplitter
import br.com.source.view.common.StatusStyle.backgroundColor
import br.com.source.view.dashboard.left.LeftContainer
import br.com.source.view.dashboard.left.LeftContainerViewModel
import br.com.source.view.dashboard.logo.LogoContainer
import br.com.source.view.dashboard.right.RightContainer
import br.com.source.view.dashboard.right.RightContainerViewModel
import br.com.source.view.dashboard.right.RightState
import br.com.source.view.dashboard.top.TopContainer
import br.com.source.view.dashboard.top.TopContainerViewModel
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState

@Composable
fun Dashboard(localRepository: LocalRepository, close: () -> Unit) {
    val initialPositionOfDivider = 0.22f
    val vSplitterState = rememberSplitPaneState(initialPositionOfDivider)
    val vSplitterStateStatic = rememberSplitPaneState(initialPositionOfDivider, false)
    val rightState = remember { mutableStateOf<RightState>(RightState.History) }
    val leftContainerViewModel = LeftContainerViewModel(localRepository)
    val rightContainerViewModel = RightContainerViewModel(localRepository)
    val topContainerViewModel = TopContainerViewModel(localRepository)

    Column {
        HorizontalSplitPane(
            splitPaneState = vSplitterStateStatic,
            modifier = Modifier.background(backgroundColor).height(65.dp),
        ) {
            first {
                Box(Modifier.fillMaxWidth().weight(0.24f)) {
                    LogoContainer()
                }
            }
            second {
                Box(Modifier.fillMaxWidth().weight(0.84f)) {
                    TopContainer(topContainerViewModel, close,
                        leftContainerReload = {
                            leftContainerViewModel.localBranches()
                            leftContainerViewModel.remoteBranches()
                            leftContainerViewModel.tags()
                            leftContainerViewModel.stashs()
                        },
                        rightContainerReload = {
                            rightContainerViewModel.history()
                        },
                        commit = {
                            rightState.value = RightState.Commit
                        }
                    )
                }
            }
            SourceHorizontalSplitter()
        }
        HorizontalSplitPane(
            splitPaneState = vSplitterState,
            modifier = Modifier.background(backgroundColor)
        ) {
            first {
                Column {
                    LeftContainer(leftContainerViewModel,
                        openStash =  {
                            rightState.value = RightState.OpenStash(it)
                        },
                        history = {
                            rightState.value = RightState.History
                        },
                        rightContainerReload = {
                            rightContainerViewModel.history()
                        }
                    )
                }
            }
            second {
                RightContainer(rightContainerViewModel, rightState)
            }
            SourceHorizontalSplitter()
        }
    }
}
