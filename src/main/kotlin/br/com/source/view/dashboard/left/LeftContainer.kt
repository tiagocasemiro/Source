package br.com.source.view.dashboard.left

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import br.com.source.model.domain.LocalRepository
import br.com.source.view.common.cardPadding
import br.com.source.view.dashboard.left.branches.*

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun LeftContainer(localRepository: LocalRepository) {
    val branchesViewModel = BranchesViewModel(localRepository)
    val localBranchesStatus = remember { mutableStateOf(branchesViewModel.localBranches()) }
    val remoteBranchesStatus = remember { mutableStateOf(branchesViewModel.remoteBranches()) }
    val tagsStatus = remember { mutableStateOf(branchesViewModel.tags()) }
    val stashsStatus = remember { mutableStateOf(branchesViewModel.stashs()) }

    Box(Modifier.fillMaxSize().padding(cardPadding)) {
        val stateVertical = rememberScrollState(0)
        Column(Modifier.verticalScroll(stateVertical)) {
            LocalBranchExpandedList(localBranchesStatus.value,
                delete = {
                    branchesViewModel.deleteLocalBranch(it)
                    localBranchesStatus.value = branchesViewModel.localBranches()
                },
                switchTo = {
                    branchesViewModel.checkoutLocalBranch(it)
                    localBranchesStatus.value = branchesViewModel.localBranches()
                })
            Spacer(Modifier.height(cardPadding))
            RemoteBranchExpandedList(remoteBranchesStatus.value,
                checkout = {
                    println("onSwitch on " + it.name)
                },
                delete = {
                   branchesViewModel.deleteRemoteBranch(it)
                   remoteBranchesStatus.value = branchesViewModel.remoteBranches()
                })
            Spacer(Modifier.height(cardPadding))
            TagExpandedList(tagsStatus.value,
                checkout = {
                    println("checkout on " + it.name)
                },
                delete = {
                    println("delete on " + it.name)
                }
            )
            Spacer(Modifier.height(cardPadding))
            StashExpandedList(stashsStatus.value,
                open = {
                    println("open on " + it.name)
                },
                apply = {
                    println("apply on " + it.name)
                },
                delete = {
                    println("delete on " + it.name)
                }
            )
            Spacer(Modifier.height(cardPadding))
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(stateVertical)
        )
    }
}

