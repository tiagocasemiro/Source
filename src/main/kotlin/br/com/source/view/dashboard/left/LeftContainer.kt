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
import br.com.source.view.model.Branch
import br.com.source.view.model.Stash
import br.com.source.view.model.Tag

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun LeftContainer(localRepository: LocalRepository, leftContainerReload: MutableState<Boolean> = mutableStateOf(false), openStash: (Stash) -> Unit, history: () -> Unit ) {
    val leftContainerViewModel = LeftContainerViewModel(localRepository)
    val localBranchesStatus = remember { mutableStateOf(emptyList<Branch>()) }
    val remoteBranchesStatus = remember { mutableStateOf(emptyList<Branch>()) }
    val tagsStatus = remember { mutableStateOf(emptyList<Tag>()) }
    val stashsStatus = remember { mutableStateOf(emptyList<Stash>()) }
    val updateStash = {
        leftContainerViewModel.stashs { message ->
            stashsStatus.value = message.retryOr(emptyList())
        }
    }
    val updateTags = {
        leftContainerViewModel.tags { message ->
            tagsStatus.value = message.retryOr(emptyList())
        }
    }
    val updateRemoteBranches = {
        leftContainerViewModel.remoteBranches { message ->
            remoteBranchesStatus.value = message.retryOr(emptyList())
            if(message.isError()) {
                showDialog("Loading error", message.message, type = TypeCommunication.error)
            }
        }
    }
    val updateLocalBranches = {
        leftContainerViewModel.localBranches { message ->
            localBranchesStatus.value = message.retryOr(emptyList())
            if(message.isError()) {
                showDialog("Loading error", message.message, type = TypeCommunication.error)
            }
        }
    }
    updateStash()
    updateTags()
    updateRemoteBranches()
    updateLocalBranches()

    if(leftContainerReload.value) {
        updateLocalBranches()
        updateRemoteBranches()
        updateStash()
        updateTags()
        leftContainerReload.value = false
    }

    Box(Modifier.fillMaxSize()) {
        val stateVertical = rememberScrollState(0)
        Column(Modifier.verticalScroll(stateVertical)) {
            LocalBranchExpandedList(localBranchesStatus.value,
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
                                leftContainerViewModel.deleteLocalBranch(it) { result ->
                                    if(result.isError()) {
                                        showActionError(result)
                                    } else {
                                        updateLocalBranches()
                                        showSuccessNotification("Local branch ${it.name} deleted with success")
                                    }
                                    hideLoad()
                                }
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
                        leftContainerViewModel.checkoutLocalBranch(it) { message ->
                            if(message.isError()) {
                                showActionError(message)
                            } else {
                                updateLocalBranches()
                                showSuccessNotification("Switch to branch ${it.name} with success")
                            }
                            hideLoad()
                        }
                    }
                },
                history = history)
            Spacer(Modifier.height(cardPadding))
            RemoteBranchExpandedList(remoteBranchesStatus.value,
                checkout = {
                    if(leftContainerViewModel.isLocalBranch(it, localBranchesStatus.value)) {
                        showNotification("This branch is already in the local repository", type = TypeCommunication.warn)
                    } else {
                        showLoad()
                        leftContainerViewModel.checkoutRemoteBranch(it) { message ->
                            message.on(
                                success = { _ ->
                                    showSuccessNotification("Checkout branch ${it.name} with success")
                                    updateLocalBranches()
                                    updateRemoteBranches()
                                    hideLoad()
                                },
                                error = { error ->
                                    showActionError(error)
                                    hideLoad()
                                }
                            )
                        }
                    }
                },
                delete = {
                    showDialogTwoButton("Dangerous action", listOf(NormalText("Do you really want to delete the remote branch"), BoldText(it.clearName)),
                        labelPositive = "Yes", actionPositive = {
                            showLoad()
                            leftContainerViewModel.deleteRemoteBranch(it) { message ->
                                message.on(
                                    error = { error ->
                                        showActionError(error)
                                        hideLoad()
                                    },
                                    success = { _ ->
                                        updateRemoteBranches()
                                        showSuccessNotification("Remote branch ${it.name} deleted with success")
                                        hideLoad()
                                    }
                                )
                            }
                        },
                        labelNegative = "No", type = TypeCommunication.warn
                    )
                }, history = history)
            Spacer(Modifier.height(cardPadding))
            TagExpandedList(tagsStatus.value,
                checkout = {
                    showLoad()
                    leftContainerViewModel.checkoutTag(it) { message ->
                        message.on(
                            success = { success ->
                                updateLocalBranches()
                                showNotification(success, type = TypeCommunication.success)
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
                    showDialogTwoButton("Dangerous action", listOf(NormalText("Do you really want to delete the tag"), BoldText(it.name)),
                        labelPositive = "Yes", actionPositive = {
                            showLoad()
                            leftContainerViewModel.delete(it) { message ->
                                message.on(
                                    success = { success ->
                                        updateTags()
                                        showNotification(success, type = TypeCommunication.success)
                                        hideLoad()
                                    },
                                    error = { error ->
                                        showActionError(error)
                                        hideLoad()
                                    }
                                )
                            }
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
                    leftContainerViewModel.applyStash(stash) { message ->
                        message.onSuccessWithDefaultError {
                            showSuccessNotification("Stash ${stash.name} applied with success")
                            hideLoad()
                        }
                    }
                },
                delete = { stash ->
                    showDialogTwoButton("Dangerous action", listOf(NormalText("Do you really want to delete the stash"), BoldText(stash.name)),
                        labelPositive = "Yes", actionPositive = {
                            showLoad()
                            leftContainerViewModel.delete(stash) { message ->
                                message.onSuccessWithDefaultError {
                                    updateStash()
                                    showSuccessNotification("Stash ${stash.name} deleted with success")
                                    hideLoad()
                                }
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

