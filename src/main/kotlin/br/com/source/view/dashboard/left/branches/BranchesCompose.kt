package br.com.source.view.dashboard.left.branches

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ContextMenuDataProvider
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
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
import br.com.source.model.util.emptyString
import br.com.source.view.common.Fonts
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
fun LocalBranchExpandedList(header: String, branches: List<Branch>, icon: String, onClickItem: (Int) -> Unit) {
    val expanded = remember { mutableStateOf(false) }
    val rotateState = animateFloatAsState(
        targetValue = if (expanded.value) 180F else 0F,
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
            shape = RoundedCornerShape(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 13.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painterResource(icon),
                    contentDescription = "Indication of expanded card",
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.size(5.dp))
                Text(
                    text = header,
                    modifier = Modifier.fillMaxWidth(0.92F),
                    fontFamily = Fonts.roboto(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = itemRepositoryText
                )
                Icon(
                    painterResource("images/arrow-icon.svg"),
                    contentDescription = "Indication of expanded card",
                    modifier = Modifier.rotate(rotateState.value).height(9.dp).width(10.dp)
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
                val tab = 32.dp
                val doubleTab = 48.dp
                branches.forEachIndexed { index, branch ->
                    if(branch.hasFolder()) {
                        if(branch.folder == lastFolderName) {
                            ItemBranch(doubleTab, branch.name, onClickItem, isHoverItem, index)
                        } else {
                            lastFolderName = branch.folder
                            ItemFolderBranch(tab, branch.folder)
                            ItemBranch(doubleTab, branch.name, onClickItem, isHoverItem, index)
                        }
                    } else {
                        lastFolderName = emptyString()
                        ItemBranch(tab, branch.name, onClickItem, isHoverItem, index)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ItemBranch(tab: Dp, label: String, onClickItem: (Int) -> Unit, isHoverItem: MutableState<Int?>, index: Int) {
    ContextMenuDataProvider(
        items = {
            listOf(
                ContextMenuItem("Switch") {
                    // todo implement
                },
                ContextMenuItem("Delete") {
                    // todo implement
                },
                ContextMenuItem("Rename") {
                    // todo implement
                }
            )
        },
    ) {
        Card(
            onClick = { onClickItem(index) },
            modifier = Modifier
                .background(Color.Transparent)
                .padding(0.dp)
                .height(32.dp),
            elevation = 0.dp,
            backgroundColor = Color.Transparent,
            shape = RoundedCornerShape(8.dp),
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
                            RoundedCornerShape(4.dp)
                        )
                        .pointerHoverIcon(PointerIcon(Cursor(Cursor.DEFAULT_CURSOR)))
                        .fillMaxSize()
                ) {
                    Spacer(Modifier.width(tab))
                    Icon(
                        painterResource("images/arrow-icon.svg"),
                        contentDescription = "Indication of expanded card",
                        modifier = Modifier.rotate(270f).height(9.dp).width(10.dp)
                    )
                    Spacer(Modifier.width(16.dp).height(24.dp))
                    Text(
                        text = label,
                        modifier = Modifier.fillMaxWidth(),
                        fontFamily = Fonts.roboto(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = itemRepositoryText,
                    )
                }
                SelectionContainer {
                    Spacer(Modifier.fillMaxSize().background(Color.Transparent))
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ItemFolderBranch(tab: Dp, label: String) {
    Card(
        modifier = Modifier
            .background(Color.Transparent)
            .padding(0.dp)
            .height(32.dp),
        elevation = 0.dp,
        backgroundColor = Color.Transparent,
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(
                    Color.Transparent,
                    RoundedCornerShape(4.dp)
                )
                .pointerHoverIcon(PointerIcon(Cursor(Cursor.DEFAULT_CURSOR)))
                .fillMaxSize()
        ) {
            Spacer(Modifier.width(tab))
            Icon(
                painterResource("images/folder-branch-icon.svg"),
                contentDescription = "Indication of expanded card",
                modifier = Modifier.height(9.dp).width(10.dp)
            )
            Spacer(Modifier.width(16.dp).height(24.dp))
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


@OptIn(ExperimentalFoundationApi::class, androidx.compose.ui.ExperimentalComposeUiApi::class)
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun RemoteBranchExpandedList(header: String, branches: List<Branch>, icon: String, onClickItem: (Int) -> Unit) {
    val expanded = remember { mutableStateOf(false) }
    val rotateState = animateFloatAsState(
        targetValue = if (expanded.value) 180F else 0F,
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
            shape = RoundedCornerShape(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 13.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painterResource(icon),
                    contentDescription = "Indication of expanded card",
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.size(5.dp))
                Text(
                    text = header,
                    modifier = Modifier.fillMaxWidth(0.92F),
                    fontFamily = Fonts.roboto(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = itemRepositoryText
                )
                Icon(
                    painterResource("images/arrow-icon.svg"),
                    contentDescription = "Indication of expanded card",
                    modifier = Modifier.rotate(rotateState.value).height(9.dp).width(10.dp)
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
                branches.forEachIndexed { index, it ->
                    ContextMenuDataProvider(
                        items = {
                            listOf(
                                ContextMenuItem("Checkout") {
                                    // todo implement
                                },
                                ContextMenuItem("Delete") {
                                    // todo implement
                                },
                                ContextMenuItem("Rename") {
                                    // todo implement
                                }
                            )
                        },
                    ) {
                        Card(
                            onClick = { onClickItem(index) },
                            modifier = Modifier
                                .background(Color.Transparent)
                                .padding(0.dp)
                                .height(32.dp),
                            elevation = 0.dp,
                            backgroundColor = Color.Transparent,
                            shape = RoundedCornerShape(8.dp),
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
                                            RoundedCornerShape(4.dp)
                                        )
                                        .pointerHoverIcon(PointerIcon(Cursor(Cursor.DEFAULT_CURSOR)))
                                        .fillMaxSize()
                                ) {
                                    Spacer(Modifier.width(32.dp))
                                    Icon(
                                        painterResource("images/arrow-icon.svg"),
                                        contentDescription = "Indication of expanded card",
                                        modifier = Modifier.rotate(270f).height(9.dp).width(10.dp)
                                    )
                                    Spacer(Modifier.width(16.dp).height(24.dp))
                                    Text(
                                        text = it.name,
                                        modifier = Modifier.fillMaxWidth(),
                                        fontFamily = Fonts.roboto(),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = itemRepositoryText,
                                    )
                                }
                                SelectionContainer {
                                    Spacer(Modifier.fillMaxSize().background(Color.Transparent))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun TagExpandedList(header: String, list: List<Tag>, icon: String, onClickItem: (Int) -> Unit) {
    val expanded = remember { mutableStateOf(false) }
    val rotateState = animateFloatAsState(
        targetValue = if (expanded.value) 180F else 0F,
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
            shape = RoundedCornerShape(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 13.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painterResource(icon),
                    contentDescription = "Indication of expanded card",
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.size(5.dp))
                Text(
                    text = header,
                    modifier = Modifier.fillMaxWidth(0.92F),
                    fontFamily = Fonts.roboto(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = itemRepositoryText
                )
                Icon(
                    painterResource("images/arrow-icon.svg"),
                    contentDescription = "Indication of expanded card",
                    modifier = Modifier.rotate(rotateState.value).height(9.dp).width(10.dp)
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
                list.forEachIndexed { index, tag ->
                    ContextMenuDataProvider(
                        items = {
                            listOf(
                                ContextMenuItem("Look") {
                                    // todo implement
                                },
                                ContextMenuItem("Delete") {
                                    // todo implement
                                }
                            )
                        },
                    ) {
                        Card(
                            onClick = { onClickItem(index) },
                            modifier = Modifier
                                .background(Color.Transparent)
                                .padding(0.dp)
                                .height(32.dp),
                            elevation = 0.dp,
                            backgroundColor = Color.Transparent,
                            shape = RoundedCornerShape(8.dp),
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
                                            RoundedCornerShape(4.dp)
                                        )
                                        .pointerHoverIcon(PointerIcon(Cursor(Cursor.DEFAULT_CURSOR)))
                                        .fillMaxSize()
                                ) {
                                    Spacer(Modifier.width(32.dp))
                                    Icon(
                                        painterResource("images/arrow-icon.svg"),
                                        contentDescription = "Indication of expanded card",
                                        modifier = Modifier.rotate(270f).height(9.dp).width(10.dp)
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
                                SelectionContainer {
                                    Spacer(Modifier.fillMaxSize().background(Color.Transparent))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun StashExpandedList(header: String, list: List<Stash>, icon: String, onClickItem: (Int) -> Unit) {
    val expanded = remember { mutableStateOf(false) }
    val rotateState = animateFloatAsState(
        targetValue = if (expanded.value) 180F else 0F,
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
            shape = RoundedCornerShape(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 13.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painterResource(icon),
                    contentDescription = "Indication of expanded card",
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.size(5.dp))
                Text(
                    text = header,
                    modifier = Modifier.fillMaxWidth(0.92F),
                    fontFamily = Fonts.roboto(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = itemRepositoryText
                )
                Icon(
                    painterResource("images/arrow-icon.svg"),
                    contentDescription = "Indication of expanded card",
                    modifier = Modifier.rotate(rotateState.value).height(9.dp).width(10.dp)
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
                list.forEachIndexed { index, stash ->
                    ContextMenuDataProvider(
                        items = {
                            listOf(
                                ContextMenuItem("Open") {
                                    // todo empliment
                                },
                                ContextMenuItem("Apply") {
                                    // todo implement
                                },
                                ContextMenuItem("Delete") {
                                    // todo empliment
                                }
                            )
                        },
                    ) {
                        Card(
                            onClick = { onClickItem(index) },
                            modifier = Modifier
                                .background(Color.Transparent)
                                .padding(0.dp)
                                .height(32.dp),
                            elevation = 0.dp,
                            backgroundColor = Color.Transparent,
                            shape = RoundedCornerShape(8.dp),
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
                                            RoundedCornerShape(4.dp)
                                        )
                                        .pointerHoverIcon(PointerIcon(Cursor(Cursor.DEFAULT_CURSOR)))
                                        .fillMaxSize()
                                ) {
                                    Spacer(Modifier.width(32.dp))
                                    Icon(
                                        painterResource("images/arrow-icon.svg"),
                                        contentDescription = "Indication of expanded card",
                                        modifier = Modifier.rotate(270f).height(9.dp).width(10.dp)
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
                                SelectionContainer {
                                    Spacer(Modifier.fillMaxSize().background(Color.Transparent))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

