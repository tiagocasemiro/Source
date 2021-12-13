package br.com.source.view.dashboard.left

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import br.com.source.view.common.*
import br.com.source.view.components.BoldText
import br.com.source.view.components.NormalText
import br.com.source.view.components.TypeCommunication
import br.com.source.view.components.showDialogTwoButton
import br.com.source.view.dashboard.left.branches.LocalBranchExpandedList
import br.com.source.view.dashboard.left.branches.RemoteBranchExpandedList
import br.com.source.view.dashboard.left.branches.StashExpandedList
import br.com.source.view.dashboard.left.branches.TagExpandedList
import br.com.source.view.model.Branch
import br.com.source.view.model.Stash
import br.com.source.view.model.Tag

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun LeftContainer(leftContainerViewModel: LeftContainerViewModel, openStash: (Stash) -> Unit, history: () -> Unit ) {
    val localBranchesStatus: State<List<Branch>> = leftContainerViewModel.localBranchesStatus.collectAsState()
    val remoteBranchesStatus: State<List<Branch>> = leftContainerViewModel.remoteBranchesStatus.collectAsState()
    val tagsStatus: State<List<Tag>> = leftContainerViewModel.tagsStatus.collectAsState()
    val stashsStatus: State<List<Stash>> = leftContainerViewModel.stashsStatus.collectAsState()
    val showLoad = leftContainerViewModel.showLoad.collectAsState()
    val updateStash = { leftContainerViewModel.stashs() }
    val updateTags = { leftContainerViewModel.tags() }
    val updateRemoteBranches = { leftContainerViewModel.remoteBranches() }
    val updateLocalBranches = { leftContainerViewModel.localBranches() }

    updateLocalBranches()
    updateRemoteBranches()
    updateStash()
    updateTags()

    LoadState(showLoad) {
        Box(Modifier.fillMaxSize().background(dialogBackgroundColor)) {
            val stateVertical = rememberScrollState(0)
            Column(Modifier.verticalScroll(stateVertical)) {
                LocalBranchExpandedList(
                    localBranchesStatus.value,
                    delete = {
                        showDialogTwoButton(
                            "Dangerous action",
                            listOf(NormalText("Do you really want to delete the local branch"), BoldText(it.clearName)),
                            labelPositive = "Yes",
                            actionPositive = {
                                leftContainerViewModel.deleteLocalBranch(it) {
                                    showSuccessNotification("Local branch ${it.name} deleted with success")
                                }
                            },
                            labelNegative = "No",
                            type = TypeCommunication.warn
                        )
                    },
                    switchTo = {
                        leftContainerViewModel.checkoutLocalBranch(it) {
                            showSuccessNotification("Switch to branch ${it.name} with success")
                        }
                    },
                    history = history
                )
                Spacer(Modifier.height(cardPadding))
                RemoteBranchExpandedList(remoteBranchesStatus.value,
                    checkout = {
                        if (leftContainerViewModel.isLocalBranch(it)) {
                            showWarnNotification("This branch is already in the local repository")
                        } else {
                            leftContainerViewModel.checkoutRemoteBranch(it) {
                                showSuccessNotification("Checkout branch ${it.name} with success")
                            }
                        }
                    },
                    delete = {
                        showDialogTwoButton(
                            "Dangerous action",
                            listOf(
                                NormalText("Do you really want to delete the remote branch"),
                                BoldText(it.clearName)
                            ),
                            labelPositive = "Yes",
                            actionPositive = {
                                leftContainerViewModel.deleteRemoteBranch(it) {
                                    showSuccessNotification("Remote branch ${it.name} deleted with success")
                                }
                            },
                            labelNegative = "No",
                            type = TypeCommunication.warn
                        )
                    },
                    history = history
                )
                Spacer(Modifier.height(cardPadding))
                TagExpandedList(tagsStatus.value,
                    checkout = {
                        leftContainerViewModel.checkoutTag(it) { success ->
                            showSuccessNotification(success)
                        }
                    },
                    delete = {
                        showDialogTwoButton(
                            "Dangerous action",
                            listOf(NormalText("Do you really want to delete the tag"), BoldText(it.name)),
                            labelPositive = "Yes",
                            actionPositive = {
                                leftContainerViewModel.delete(it) { success ->
                                    showSuccessNotification(success)
                                }
                            },
                            labelNegative = "No",
                            type = TypeCommunication.warn
                        )
                    }
                )
                Spacer(Modifier.height(cardPadding))
                StashExpandedList(stashsStatus.value,
                    open = {
                        openStash(it)
                    },
                    apply = { stash ->
                        leftContainerViewModel.applyStash(stash) {
                            showSuccessNotification("Stash ${stash.name} applied with success")
                        }
                    },
                    delete = { stash ->
                        showDialogTwoButton(
                            "Dangerous action",
                            listOf(NormalText("Do you really want to delete the stash"), BoldText(stash.name)),
                            labelPositive = "Yes",
                            actionPositive = {
                                leftContainerViewModel.delete(stash) {
                                    showSuccessNotification("Stash ${stash.name} deleted with success")
                                }
                            },
                            labelNegative = "No",
                            type = TypeCommunication.warn
                        )
                    }
                )
                Spacer(Modifier.height(cardPadding))
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().padding(horizontal = paddingScrollBar),
                adapter = rememberScrollbarAdapter(stateVertical)
            )
        }
    }
}

