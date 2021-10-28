package br.com.source.view.dashboard.left

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import br.com.source.model.domain.LocalRepository
import br.com.source.view.common.cardPadding
import br.com.source.view.dashboard.left.branches.*


@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun LeftContainer(localRepository: LocalRepository) {
    val branchesViewModel = BranchesViewModel(localRepository)
    Box(Modifier.fillMaxSize().padding(cardPadding)) {
        val stateVertical = rememberScrollState(0)
        Column(Modifier.verticalScroll(stateVertical)) {
            LocalBranchExpandedList("Branch local", branchesViewModel.localBranches(), "images/local-branch-icon.svg") {

            }
            Spacer(Modifier.height(cardPadding))
            RemoteBranchExpandedList("Branch remote", branchesViewModel.remoteBranches(), "images/remote-branch-icon.svg") {

            }
            Spacer(Modifier.height(cardPadding))
            TagExpandedList("Tag", branchesViewModel.tags(), "images/tag-icon.svg") {

            }
            Spacer(Modifier.height(cardPadding))
            StashExpandedList("Stash", branchesViewModel.stashs(), "images/stash-icon.svg") {

            }
            Spacer(Modifier.height(cardPadding))
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(stateVertical)
        )
    }
}

