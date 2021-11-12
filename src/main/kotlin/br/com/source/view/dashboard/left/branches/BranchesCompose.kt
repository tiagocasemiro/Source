package br.com.source.view.dashboard.left.branches

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.source.model.util.conditional
import br.com.source.model.util.detectTapGesturesWithContextMenu
import br.com.source.model.util.emptyString
import br.com.source.view.common.Fonts
import br.com.source.view.common.SourceTooltip
import br.com.source.view.common.itemBranchHoveBackground
import br.com.source.view.common.itemRepositoryText
import br.com.source.view.model.Branch
import br.com.source.view.model.Stash
import br.com.source.view.model.Tag
import java.awt.Cursor

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun LocalBranchExpandedList(branches: List<Branch>, switchTo: (Branch) -> Unit, delete: (Branch) -> Unit, history: () -> Unit) {
    val expanded = remember { mutableStateOf(false) }
    val rotateState = animateFloatAsState(
        targetValue = if(expanded.value) 0F else -90F
    )
    val isHoverItem = mutableStateOf<Int?>(null)
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            onClick = { expanded.value = expanded.value.not() },
            modifier = Modifier
                .background(Color.Transparent)
                .padding(0.dp)
                .height(32.dp),
            elevation = 0.dp,
            backgroundColor = Color.Transparent,
            shape = RoundedCornerShape(0.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 13.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painterResource("images/local-branch-icon.svg"),
                    contentDescription = "Indication of expanded card",
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.size(10.dp))
                Text(
                    text = "Branch local",
                    modifier = Modifier.fillMaxWidth(),
                    fontFamily = Fonts.roboto(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = itemRepositoryText
                )
            }
        }
        AnimatedVisibility(
            visible = expanded.value,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
            ) {
                var lastFolderName = emptyString()
                EmptyStateItem(branches.isEmpty()) {
                    branches.forEachIndexed { index, branch ->
                        val items = {
                            listOf(
                                ContextMenuItem("Switch") {
                                    switchTo(branch)
                                },
                                ContextMenuItem("Delete") {
                                    delete(branch)
                                },
                            )
                        }
                        if (branch.hasFolder()) {
                            if (branch.folder == lastFolderName) {
                                ItemBranchCompose(48.dp, branch, { history() }, { switchTo(branch) }, isHoverItem, index, items)
                            } else {
                                lastFolderName = branch.folder
                                ItemFolderBranchCompose(32.dp, branch.folder)
                                ItemBranchCompose(48.dp, branch, { history() }, { switchTo(branch) }, isHoverItem, index, items)
                            }
                        } else {
                            lastFolderName = emptyString()
                            ItemBranchCompose(32.dp, branch, { history() }, { switchTo(branch) }, isHoverItem, index, items)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ItemBranchCompose(tab: Dp, branch: Branch, onTap: () -> Unit, onDoubleClickItem: () -> Unit, isHoverItem: MutableState<Int?>, index: Int, items: () -> List<ContextMenuItem>) {
    val state: ContextMenuState = remember { ContextMenuState() }
    ContextMenuArea(
        items = items,
        state = state
    ) {
        Card(
            modifier = Modifier
                .background(Color.Transparent)
                .padding(0.dp)
                .height(32.dp)
                .detectTapGesturesWithContextMenu(state = state, onDoubleTap = {
                    onDoubleClickItem()
                }, onTap = {
                    onTap()
                }),
            elevation = 0.dp,
            backgroundColor = Color.Transparent,
            shape = RoundedCornerShape(0.dp),
        ) {
            Box(Modifier.fillMaxSize()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .pointerMoveFilter(
                            onEnter = {
                                isHoverItem.value = index
                                return@pointerMoveFilter false
                            },
                            onExit = {
                                isHoverItem.value = null
                                return@pointerMoveFilter false
                            }
                        )
                        .background(
                            if (isHoverItem.value == index) itemBranchHoveBackground else Color.Transparent,
                            RoundedCornerShape(0.dp)
                        )
                        .pointerHoverIcon(PointerIcon(Cursor(Cursor.DEFAULT_CURSOR)))
                        .fillMaxSize()
                ) {
                    Spacer(Modifier.width(tab))
                    Icon(
                        painterResource("images/arrow-icon.svg"),
                        contentDescription = "Indication of expanded card",
                        modifier = Modifier.rotate(270f).height(12.dp).width(10.dp)
                    )
                    Spacer(Modifier.width(8.dp).height(24.dp))
                    Text(
                        text = branch.name,
                        modifier = Modifier.fillMaxWidth(),
                        fontFamily =  if (branch.isCurrent) Fonts.balooBhai2() else Fonts.roboto(),
                        fontSize = if (branch.isCurrent) 18.sp else 14.sp,
                        fontWeight =  if (branch.isCurrent) FontWeight.ExtraBold else FontWeight.Normal,
                        color = itemRepositoryText,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ItemFolderBranchCompose(tab: Dp, label: String) {
    Card(
        modifier = Modifier
            .background(Color.Transparent)
            .padding(0.dp)
            .height(32.dp),
        elevation = 0.dp,
        backgroundColor = Color.Transparent,
        shape = RoundedCornerShape(0.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(
                    Color.Transparent,
                    RoundedCornerShape(0.dp)
                )
                .pointerHoverIcon(PointerIcon(Cursor(Cursor.DEFAULT_CURSOR)))
                .fillMaxSize()
        ) {
            Spacer(Modifier.width(tab))
            Icon(
                painterResource("images/folder-branch-icon.svg"),
                contentDescription = "Indication of expanded card",
                modifier = Modifier.height(10.dp).width(12.dp)
            )
            Spacer(Modifier.width(8.dp).height(24.dp))
            Text(
                text = label,
                modifier = Modifier.fillMaxWidth(),
                fontFamily = Fonts.roboto(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = itemRepositoryText,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun RemoteBranchExpandedList(branches: List<Branch>, checkout: (Branch) -> Unit, delete: (Branch) -> Unit, history: () -> Unit) {
    val expanded = remember { mutableStateOf(false) }
    val rotateState = animateFloatAsState(
        targetValue = if (expanded.value) 0F else -90F,
    )
    val isHoverItem = mutableStateOf<Int?>(null)
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            onClick = { expanded.value = expanded.value.not() },
            modifier = Modifier
                .background(Color.Transparent)
                .padding(0.dp)
                .height(32.dp),
            elevation = 0.dp,
            backgroundColor = Color.Transparent,
            shape = RoundedCornerShape(0.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 13.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painterResource("images/remote-branch-icon.svg"),
                    contentDescription = "Indication of expanded card",
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.size(10.dp))
                Text(
                    text = "Branch remote",
                    modifier = Modifier.fillMaxWidth(),
                    fontFamily = Fonts.roboto(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = itemRepositoryText
                )
            }
        }
        AnimatedVisibility(
            visible = expanded.value,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
            ) {
                var lastFolderName = emptyString()
                EmptyStateItem(branches.isEmpty()) {
                    branches.forEachIndexed { index, branch ->
                        val items = {
                            listOf(
                                ContextMenuItem("Checkout") {
                                    checkout(branch)
                                },
                                ContextMenuItem("Delete") {
                                    delete(branch)
                                },
                            )
                        }
                        if (branch.hasFolder()) {
                            if (branch.folder == lastFolderName) {
                                ItemBranchCompose(48.dp, branch, { history() }, { checkout(branch) }, isHoverItem, index, items)
                            } else {
                                lastFolderName = branch.folder
                                ItemFolderBranchCompose(32.dp, branch.folder)
                                ItemBranchCompose(48.dp, branch, { history() }, { checkout(branch) }, isHoverItem, index, items)
                            }
                        } else {
                            lastFolderName = emptyString()
                            ItemBranchCompose(32.dp, branch, { history() }, { checkout(branch) }, isHoverItem, index, items)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun TagExpandedList(list: List<Tag>, checkout: (Tag) -> Unit, delete: (Tag) -> Unit) {
    val expanded = remember { mutableStateOf(false) }
    val rotateState = animateFloatAsState(
        targetValue = if (expanded.value) 0F else -90F,
    )
    val isHoverItem = mutableStateOf<Int?>(null)
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            onClick = { expanded.value = expanded.value.not() },
            modifier = Modifier
                .background(Color.Transparent)
                .padding(0.dp)
                .height(32.dp),
            elevation = 0.dp,
            backgroundColor = Color.Transparent,
            shape = RoundedCornerShape(0.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 13.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painterResource("images/tag-icon.svg"),
                    contentDescription = "Indication of expanded card",
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.size(10.dp))
                Text(
                    text = "Tag",
                    modifier = Modifier.fillMaxWidth(),
                    fontFamily = Fonts.roboto(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = itemRepositoryText
                )
            }
        }
        AnimatedVisibility(
            visible = expanded.value,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
            ) {
                EmptyStateItem(list.isEmpty()) {
                    list.forEachIndexed { index, tag ->
                        val state: ContextMenuState = remember { ContextMenuState() }
                        ContextMenuArea(
                            items = {
                                listOf(
                                    ContextMenuItem("Checkout") {
                                        checkout(tag)
                                    },
                                    ContextMenuItem("Delete") {
                                        delete(tag)
                                    }
                                )
                            },
                            state = state
                        ) {
                            Card(
                                modifier = Modifier
                                    .background(Color.Transparent)
                                    .padding(0.dp)
                                    .height(32.dp)
                                    .detectTapGesturesWithContextMenu(state = state, onDoubleTap = {
                                        checkout(tag)
                                    }),
                                elevation = 0.dp,
                                backgroundColor = Color.Transparent,
                                shape = RoundedCornerShape(0.dp),
                            ) {
                                Box(Modifier.fillMaxSize()) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .pointerMoveFilter(
                                                onEnter = {
                                                    isHoverItem.value = index
                                                    return@pointerMoveFilter false
                                                },
                                                onExit = {
                                                    isHoverItem.value = null
                                                    return@pointerMoveFilter false
                                                }
                                            )
                                            .background(
                                                if (isHoverItem.value == index) itemBranchHoveBackground else Color.Transparent,
                                                RoundedCornerShape(0.dp)
                                            )
                                            .pointerHoverIcon(PointerIcon(Cursor(Cursor.DEFAULT_CURSOR)))
                                            .fillMaxSize()
                                    ) {
                                        Spacer(Modifier.width(32.dp))
                                        Icon(
                                            painterResource("images/arrow-icon.svg"),
                                            contentDescription = "Indication of expanded card",
                                            modifier = Modifier.rotate(270f).height(12.dp).width(10.dp)
                                        )
                                        Spacer(Modifier.width(16.dp).height(24.dp))
                                        Text(
                                            text = tag.name,
                                            modifier = Modifier.fillMaxWidth(),
                                            fontFamily = Fonts.roboto(),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Normal,
                                            color = itemRepositoryText,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun StashExpandedList(list: List<Stash>, open: (Stash) -> Unit, apply: (Stash) -> Unit, delete: (Stash) -> Unit) {
    val expanded = remember { mutableStateOf(false) }
    val rotateState = animateFloatAsState(
        targetValue = if (expanded.value) 0F else -90F,
    )
    val isHoverItem = mutableStateOf<Int?>(null)
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            onClick = { expanded.value = expanded.value.not() },
            modifier = Modifier
                .background(Color.Transparent)
                .padding(0.dp)
                .height(32.dp),
            elevation = 0.dp,
            backgroundColor = Color.Transparent,
            shape = RoundedCornerShape(0.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 13.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painterResource("images/stash-icon.svg"),
                    contentDescription = "Indication of expanded card",
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.size(10.dp))
                Text(
                    text = "Stash",
                    modifier = Modifier.fillMaxWidth(),
                    fontFamily = Fonts.roboto(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = itemRepositoryText
                )
            }
        }
        AnimatedVisibility(
            visible = expanded.value,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
            ) {
                EmptyStateItem(list.isEmpty()) {
                    list.forEachIndexed { index, stash ->
                        val state: ContextMenuState = remember { ContextMenuState() }
                        ContextMenuArea(
                            items = {
                                listOf(
                                    ContextMenuItem("Open") {
                                        open(stash)
                                    },
                                    ContextMenuItem("Apply") {
                                        apply(stash)
                                    },
                                    ContextMenuItem("Delete") {
                                        delete(stash)
                                    }
                                )
                            },
                            state = state
                        ) {
                            SourceTooltip(stash.shortMessage) {
                                Card(
                                    modifier = Modifier
                                        .background(Color.Transparent)
                                        .padding(0.dp)
                                        .height(32.dp)
                                        .detectTapGesturesWithContextMenu(state = state,
                                            onDoubleTap = {
                                                apply(stash)
                                            },
                                            onTap = {
                                                open(stash)
                                            }
                                        ),
                                    elevation = 0.dp,
                                    backgroundColor = Color.Transparent,
                                    shape = RoundedCornerShape(0.dp),
                                ) {
                                    Box(Modifier.fillMaxSize()) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .pointerMoveFilter(
                                                    onEnter = {
                                                        isHoverItem.value = index
                                                        return@pointerMoveFilter false
                                                    },
                                                    onExit = {
                                                        isHoverItem.value = null
                                                        return@pointerMoveFilter false
                                                    }
                                                )
                                                .background(
                                                    if (isHoverItem.value == index) itemBranchHoveBackground else Color.Transparent,
                                                    RoundedCornerShape(0.dp)
                                                )
                                                .pointerHoverIcon(PointerIcon(Cursor(Cursor.DEFAULT_CURSOR)))
                                                .fillMaxSize()
                                        ) {
                                            Spacer(Modifier.width(32.dp))
                                            Icon(
                                                painterResource("images/arrow-icon.svg"),
                                                contentDescription = "Indication of expanded card",
                                                modifier = Modifier.rotate(270f).height(12.dp).width(10.dp)
                                            )
                                            Spacer(Modifier.width(16.dp).height(24.dp))
                                            Text(
                                                text = stash.name,
                                                modifier = Modifier.fillMaxWidth(),
                                                fontFamily = Fonts.roboto(),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Normal,
                                                color = itemRepositoryText,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateItem(isEmpty: Boolean, content: @Composable () -> Unit) {
    if(isEmpty) {
        Card(
            modifier = Modifier
                .background(Color.Transparent)
                .padding(0.dp)
                .height(32.dp),
            elevation = 0.dp,
            backgroundColor = Color.Transparent,
            shape = RoundedCornerShape(0.dp),

            ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Empty",
                    fontFamily = Fonts.roboto(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light,
                    color = itemRepositoryText,
                )
            }
        }
    } else {
        content()
    }
}

