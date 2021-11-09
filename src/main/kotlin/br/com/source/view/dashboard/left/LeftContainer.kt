package br.com.source.view.dashboard.left

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import br.com.source.model.domain.LocalRepository
import br.com.source.view.common.*
import br.com.source.view.components.*
import br.com.source.view.dashboard.left.branches.*
import br.com.source.view.model.Stash

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun LeftContainer(localRepository: LocalRepository, leftContainerReload: MutableState<Boolean> = mutableStateOf(false), openStash: (Stash) -> Unit, history: () -> Unit ) {
    val leftContainerViewModel = LeftContainerViewModel(localRepository)
    val localBranchesStatus = remember { mutableStateOf(leftContainerViewModel.localBranches()) }
    val remoteBranchesStatus = remember { mutableStateOf(leftContainerViewModel.remoteBranches()) }
    val tagsStatus = remember { mutableStateOf(leftContainerViewModel.tags()) }
    val stashsStatus = remember { mutableStateOf(leftContainerViewModel.stashs()) }

    if(leftContainerReload.value) {
        localBranchesStatus.value = leftContainerViewModel.localBranches()
        remoteBranchesStatus.value = leftContainerViewModel.remoteBranches()
        tagsStatus.value = leftContainerViewModel.tags()
        stashsStatus.value = leftContainerViewModel.stashs()
        leftContainerReload.value = false
    }

    Box(Modifier.fillMaxSize()) {
        val stateVertical = rememberScrollState(0)
        Column(Modifier.verticalScroll(stateVertical)) {
            if(localBranchesStatus.value.isError()) {
                showDialog("Loading error", localBranchesStatus.value.message, type = TypeCommunication.error)
            }
            LocalBranchExpandedList(localBranchesStatus.value.retryOr(emptyList()),
                delete = {
                    if(it.isCurrent) {
                        showNotification(
                            "Can not delete this branch.\nThis is current branch.",
                            type = TypeCommunication.error
                        )
                    } else {
                        showDialogTwoButton("Dangerous action", listOf(NormalText("Do you really want to delete the local branch"), BoldText(it.clearName)),
                            labelPositive = "Yes", actionPositive = {
                                showLoad()
                                val result = leftContainerViewModel.deleteLocalBranch(it)
                                if(result.isError()) {
                                    showActionError(result)
                                } else {
                                    localBranchesStatus.value = leftContainerViewModel.localBranches()
                                    showSuccessNotification("Local branch ${it.name} deleted with success")
                                }
                                hideLoad()
                            },
                            labelNegative = "No", type = TypeCommunication.warn
                        )
                    }
                },
                switchTo = {
                    if(it.isCurrent) {
                        showNotification(
                            "Can not switch to this branch.\nThis is current branch.",
                            type = TypeCommunication.warn
                        )
                    } else {
                        showLoad()
                        val result = leftContainerViewModel.checkoutLocalBranch(it)
                        if(result.isError()) {
                            showActionError(result)
                        } else {
                            localBranchesStatus.value = leftContainerViewModel.localBranches()
                            showSuccessNotification("Switch to branch ${it.name} with success")
                        }
                        hideLoad()
                    }
                },
                history = history)
            Spacer(Modifier.height(cardPadding))
            if(remoteBranchesStatus.value.isError()) {
                showDialog("Loading error", remoteBranchesStatus.value.message, type = TypeCommunication.error)
            }
            RemoteBranchExpandedList(remoteBranchesStatus.value.retryOr(emptyList()),
                checkout = {
                    if(leftContainerViewModel.isLocalBranch(it, localBranchesStatus.value.retryOr(emptyList()))) {
                        showNotification("This branch is already in the local repository", type = TypeCommunication.warn)
                    } else {
                        showLoad()
                        leftContainerViewModel.checkoutRemoteBranch(it).on(
                            success = { _ ->
                                showSuccessNotification("Checkout branch ${it.name} with success")
                                localBranchesStatus.value = leftContainerViewModel.localBranches()
                                remoteBranchesStatus.value = leftContainerViewModel.remoteBranches()
                                hideLoad()
                            },
                            error = { error ->
                                showActionError(error)
                                hideLoad()
                            }
                        )
                    }
                },
                delete = {
                    showDialogTwoButton("Dangerous action", listOf(NormalText("Do you really want to delete the remote branch"), BoldText(it.clearName)),
                        labelPositive = "Yes", actionPositive = {
                            showLoad()
                            leftContainerViewModel.deleteRemoteBranch(it).on(
                                error = { error ->
                                    showActionError(error)
                                    hideLoad()
                                },
                                success = { _ ->
                                    remoteBranchesStatus.value = leftContainerViewModel.remoteBranches()
                                    showSuccessNotification("Remote branch ${it.name} deleted with success")
                                    hideLoad()
                                }
                            )
                        },
                        labelNegative = "No", type = TypeCommunication.warn
                    )
                }, history = history)
            Spacer(Modifier.height(cardPadding))
            TagExpandedList(tagsStatus.value,
                checkout = {
                    showLoad()
                    leftContainerViewModel.checkoutTag(it).on(
                        success = { success ->
                            localBranchesStatus.value = leftContainerViewModel.localBranches()
                            showNotification(success, type = TypeCommunication.success)
                            hideLoad()
                        },
                        error = { error ->
                            showActionError(error)
                            hideLoad()
                        }
                    )
                },
                delete = {
                    showDialogTwoButton("Dangerous action", listOf(NormalText("Do you really want to delete the tag"), BoldText(it.name)),
                        labelPositive = "Yes", actionPositive = {
                            showLoad()
                            leftContainerViewModel.delete(it).on(
                                success = { success ->
                                    tagsStatus.value = leftContainerViewModel.tags()
                                    showNotification(success, type = TypeCommunication.success)
                                    hideLoad()
                                },
                                error = { error ->
                                    showActionError(error)
                                    hideLoad()
                                }
                            )
                        },
                        labelNegative = "No", type = TypeCommunication.warn
                    )
                }
            )
            Spacer(Modifier.height(cardPadding))
            StashExpandedList(stashsStatus.value,
                open = {
                    openStash(it)
                },
                apply = { stash ->
                    showLoad()
                    leftContainerViewModel.applyStash(stash).onSuccessWithDefaultError {
                        showSuccessNotification("Stash ${stash.name} applied with success")
                        hideLoad()
                    }
                },
                delete = { stash ->
                    showDialogTwoButton("Dangerous action", listOf(NormalText("Do you really want to delete the stash"), BoldText(stash.name)),
                        labelPositive = "Yes", actionPositive = {
                            showLoad()
                            leftContainerViewModel.delete(stash).onSuccessWithDefaultError {
                                stashsStatus.value = leftContainerViewModel.stashs()
                                showSuccessNotification("Stash ${stash.name} deleted with success")
                                hideLoad()
                            }
                        },
                        labelNegative = "No", type = TypeCommunication.warn
                    )
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

