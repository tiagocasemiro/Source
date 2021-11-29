package br.com.source.view.common

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.PointerIconDefaults
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.source.model.util.detectTapGesturesWithContextMenu
import br.com.source.model.util.emptyString
import br.com.source.view.components.SourceTooltipArea
import br.com.source.view.components.TooltipPlacement
import br.com.source.view.components.TypeCommunication
import br.com.source.view.dashboard.left.branches.EmptyStateItem
import br.com.source.view.model.*
import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.stage.DirectoryChooser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eclipse.jgit.diff.DiffEntry
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.SplitPaneScope
import java.awt.Cursor
import java.io.File
import javax.swing.JFileChooser
import javax.swing.JPanel
import javax.swing.filechooser.FileSystemView

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SourceChooserFolderButton(onClick: () -> Unit) {
    Image(
        painter = painterResource("images/folder-icon.svg"),
        contentDescription = "Button select directory of repository",
        modifier = Modifier
            .clickable {
                onClick()
            }
            .pointerHoverIcon(PointerIconDefaults.Hand)
            .background(Color.Transparent)
            .size(13.dp, 11.dp)
    )
}

@Composable
fun SourceChooseFolderDialog(pathRemember: MutableState<String>) {
    SwingPanel(
        background = Color.Transparent,
        modifier = Modifier.size(0.dp, 0.dp),
        factory = {
            JFXPanel()
        },
        update = {
            Platform.runLater {
                val chooser = DirectoryChooser()
                chooser.initialDirectory = File(pathRemember.value.ifEmpty { FileSystemView.getFileSystemView().defaultDirectory.path })
                chooser.title = "Select root directory of repository"
                val returnVal = chooser.showDialog(null)
                if (returnVal != null) {
                    pathRemember.value = returnVal.absolutePath
                }
            }
        }
    )
}

@Composable
fun SourceSwingChooseFolderDialog(pathRemember: MutableState<String>) {
    SwingPanel(
        background = Color.Transparent,
        modifier = Modifier.size(0.dp, 0.dp),
        factory = {
            JPanel()
        },
        update = { pane ->
            val chooser = JFileChooser()
            chooser.currentDirectory = File(pathRemember.value.ifEmpty { System.getProperty("user.home") })
            chooser.dialogTitle = "Select root directory of repository"
            chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            val returnVal = chooser.showOpenDialog(pane)
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                val file = chooser.selectedFile
                pathRemember.value = file.absolutePath
            }
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.cursorForHorizontalResize(): Modifier =
    pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.cursorForVerticalResize(): Modifier =
    pointerHoverIcon(PointerIcon(Cursor(Cursor.S_RESIZE_CURSOR)))

@OptIn(ExperimentalSplitPaneApi::class)
fun SplitPaneScope.SourceHorizontalSplitter() = splitter {
    visiblePart {
        Box(
            Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(itemRepositoryBackground)
        )
    }
    handle {
        Box(
            Modifier
                .markAsHandle()
                .cursorForHorizontalResize()
                .background(SolidColor(Color.Transparent), alpha = 0.50f)
                .width(10.dp)
                .fillMaxHeight()
        )
    }
}

@OptIn(ExperimentalSplitPaneApi::class)
fun SplitPaneScope.SourceVerticalSplitter() = splitter {
    visiblePart {
        Box(
            Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(itemRepositoryBackground)
        )
    }
    handle {
        Box(
            Modifier
                .markAsHandle()
                .cursorForVerticalResize()
                .background(SolidColor(Color.Transparent), alpha = 0.50f)
                .height(10.dp)
                .fillMaxWidth()
        )
    }
}

private val loadState = mutableStateOf(false)

fun showLoad() {
    loadState.value = true
}

fun hideLoad() {
    loadState.value = false
}

@Composable
fun Load(content: @Composable BoxScope.() -> Unit) {
    Box(Modifier.fillMaxSize()) {
       content()
       if(loadState.value) {
           Box(
               Modifier.background(Color(0,0,0, 50)).fillMaxSize().pointerInput(Unit) {
                   detectTapGestures()
               },
               contentAlignment = Alignment.Center,
           ) {
               CircularProgressIndicator(
                   Modifier.background(
                       Color(255,255,255, 200),
                       RoundedCornerShape(8.dp)
                   ).padding(8.dp)
               )
           }
        }
    }
}

private val snackBarHostState = SnackbarHostState()
private val displaySnackBar = mutableStateOf<NotificationData?>(null)

private data class NotificationData(
    val message: String,
    val type: TypeCommunication = TypeCommunication.none
)

fun showNotification(message: String, type: TypeCommunication = TypeCommunication.none) {
    displaySnackBar.value = NotificationData(message, type)
}

fun showSuccessNotification(message: String) = showNotification(message, TypeCommunication.success)

@Composable
fun createSnackBar() {
    if(displaySnackBar.value != null) {
        val notificationData: NotificationData = displaySnackBar.value!!
        SnackbarHost(
            hostState = snackBarHostState,
            snackbar = { data ->
                Snackbar(
                    modifier = Modifier.padding(16.dp).width(500.dp),
                    content = {
                        Text(
                            text = data.message,
                            style = TextStyle(
                                color = Color.White,
                                fontFamily = Fonts.roboto(),
                            ),
                            modifier = Modifier
                        )
                    },
                    backgroundColor = notificationData.type.on(
                        info = { SuccessColor.color },
                        warn = { WarnColor.color},
                        error = { ErrorColor.color },
                        none = { InfoColor.color },
                        success = { SuccessColor.color }
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End)
        )
        CoroutineScope(Dispatchers.Main).launch {
            snackBarHostState.showSnackbar(
                message = notificationData.message,
            )
            displaySnackBar.value = null
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SourceTooltip(message: String,
    forceCloseTooltip: MutableState<Boolean> = mutableStateOf(false),
    forceOpenTooltip: MutableState<Boolean> = mutableStateOf(false),
    content: @Composable () -> Unit) {
    SourceTooltipArea(
        tooltip = {
            // composable tooltip content
            Box(
                Modifier.background(Color.Transparent).padding(end = 13.dp)
            ) {
                Surface(
                    modifier = Modifier.shadow(10.dp),
                    color = Color(245, 245, 240),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(4.dp),
                        style = TextStyle(
                            fontFamily = Fonts.roboto(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = itemRepositoryText
                        )
                    )
                }
            }
        },
        delayMillis = 600, // in milliseconds
        tooltipPlacement = TooltipPlacement.CursorPoint(
            alignment = Alignment.BottomEnd,
            offset = DpOffset(10.dp, 0.dp)
        ),
        forceClose = forceCloseTooltip,
        forceOpen = forceOpenTooltip,
        content = content
    )
}

@Composable
fun FileDiffCompose(diff: Diff) {
    Column {
        Row(
            Modifier.height(32.dp).fillMaxWidth().background(cardBackgroundColor),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val resourcePath = when(diff.changeType) {
                DiffEntry.ChangeType.ADD -> "images/diff/ic-add-file.svg"
                DiffEntry.ChangeType.COPY -> "images/diff/ic-copy-file.svg"
                DiffEntry.ChangeType.DELETE -> "images/diff/ic-remove-file.svg"
                DiffEntry.ChangeType.MODIFY -> "images/diff/ic-modify-file.svg"
                DiffEntry.ChangeType.RENAME -> "images/diff/ic-rename-file.svg"
            }
            val contentDescription = when(diff.changeType) {
                DiffEntry.ChangeType.ADD -> "icon modification type add file"
                DiffEntry.ChangeType.COPY -> "icon modification type copy file"
                DiffEntry.ChangeType.DELETE -> "icon modification type remove file"
                DiffEntry.ChangeType.MODIFY -> "icon modification type modify file"
                DiffEntry.ChangeType.RENAME -> "icon modification type rename file"
            }
            Spacer(Modifier.size(10.dp))
            Icon(
                painterResource(resourcePath),
                contentDescription = contentDescription,
                modifier = Modifier.size(17.dp)
            )
            SourceTooltip(diff.fileName) {
                Text(
                    text = diff.fileName.split("/").last(),
                    modifier = Modifier.padding(start = 10.dp),
                    fontFamily = Fonts.roboto(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = itemRepositoryText,
                    textAlign = TextAlign.Left,
                    maxLines = 1
                )
            }
        }
        diff.changes.forEachIndexed { index, change ->
            ChangeCompose(change = change, index = index)
        }
    }
}

@Composable
fun ChangeCompose(change: Change, index: Int) {
    Column {
        Spacer(Modifier.height(1.dp).fillMaxWidth().background(itemRepositoryBackground))
        Column(
            Modifier.background(dialogBackgroundColor).fillMaxWidth().height(25.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Change ${index + 1}: ${change.changePosition}",
                modifier = Modifier.padding(start = 70.dp),
                fontFamily = Fonts.roboto(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = itemRepositoryText,
                textAlign = TextAlign.Left
            )
        }
        Spacer(Modifier.height(1.dp).fillMaxWidth().background(itemRepositoryBackground))
        Row {
            Column(Modifier.width(60.dp)) {
                change.lines.forEach { line ->
                    Row (
                        modifier = Modifier.background(dialogBackgroundColor).height(25.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Spacer(Modifier.width(1.dp).fillMaxHeight().background(itemRepositoryBackground))
                        Text(
                            text = if(line.numberOld == null) emptyString() else line.numberOld.toString(),
                            modifier = Modifier.weight(1f),
                            fontFamily = Fonts.roboto(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            color = itemRepositoryText,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.width(1.dp).fillMaxHeight().background(itemRepositoryBackground))
                        Text(
                            text = if(line.numberNew == null) emptyString() else line.numberNew.toString(),
                            modifier = Modifier.weight(1f),
                            fontFamily = Fonts.roboto(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            color = itemRepositoryText,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.width(1.dp).fillMaxHeight().background(itemRepositoryBackground))
                    }
                }
            }
            Box {
                val listState = rememberScrollState()
                Column(
                    Modifier.fillMaxWidth()
                ) {
                    change.lines.forEach { line ->
                        val background = when(line) {
                            is Line.Add -> Color(230,245,230)
                            is Line.Remove -> Color(245,230,230)
                            else -> Color.Transparent
                        }
                        Spacer(Modifier.height(25.dp).background(background).fillMaxWidth().absolutePadding(left = 52.dp))
                    }
                }
                Column(
                    Modifier.fillMaxWidth().horizontalScroll(listState).fillMaxWidth()
                ) {
                    change.lines.forEach { line ->
                        Row(
                            Modifier.height(25.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val textColor = when(line) {
                                is Line.Add -> Color(0,100,0)
                                is Line.Remove -> Color(100,0,0)
                                else -> itemRepositoryText
                            }
                            Text(
                                text = line.content,
                                modifier = Modifier.padding(horizontal = 10.dp),
                                fontFamily = Fonts.roboto(),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = textColor,
                                textAlign = TextAlign.Left,
                            )
                        }
                    }
                }
                HorizontalScrollbar(
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                    adapter = rememberScrollbarAdapter(
                        scrollState = listState
                    )
                )
            }
        }
    }
}

@Composable
fun FullScrollBox(modifier: Modifier = Modifier, verticalStateList: ScrollState = rememberScrollState(), horizontalStateList: ScrollState = rememberScrollState(), content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        Box(modifier.verticalScroll(verticalStateList).horizontalScroll(horizontalStateList).fillMaxSize()) {
            content()
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(
                scrollState = verticalStateList
            )
        )
        HorizontalScrollbar(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            adapter = rememberScrollbarAdapter(
                scrollState = horizontalStateList
            )
        )
    }
}

@Composable
fun VerticalScrollBox(modifier: Modifier = Modifier, verticalStateList: ScrollState = rememberScrollState(), content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        Box(modifier.verticalScroll(verticalStateList).fillMaxSize()) {
            content()
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(
                scrollState = verticalStateList
            )
        )
    }
}

@Composable
fun VerticalDivider() {
    Spacer(Modifier.background(itemRepositoryBackground).fillMaxHeight().width(1.dp))
}

@Composable
fun HorizontalDivider() {
    Spacer(Modifier.background(itemRepositoryBackground).height(1.dp).fillMaxWidth())
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun FilesChangedCompose(title: String, resume: String? = null, files: MutableState<List<FileCommit>>, onClick: MutableState<FileCommit?> = mutableStateOf(null), onDoubleClick: MutableState<FileCommit?> = mutableStateOf(null), itemsContextMenu: List<Pair<String, MutableState<FileCommit?>>> = emptyList()) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            Modifier.background(cardBackgroundColor).fillMaxWidth().height(25.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text( title,
                modifier = Modifier.padding(start = 10.dp),
                fontFamily = Fonts.balooBhai2(),
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = itemRepositoryText,
                textAlign = TextAlign.Left
            )
            Spacer(Modifier.fillMaxWidth().weight(1f))
            if(resume != null) {
                SourceTooltip(resume) {
                    Row(modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painterResource("images/info_icon.svg"),
                            contentDescription = "Details from commit",
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
                Spacer(Modifier.width(20.dp))
            }
        }
        HorizontalDivider()
        EmptyStateItem(files.value.isEmpty()) {
            Box {
                val verticalStateList: ScrollState = rememberScrollState()
                VerticalScrollBox(verticalStateList = verticalStateList) {
                    Column(Modifier.fillMaxSize()) {
                        files.value.forEachIndexed { index, _ ->
                            val color = if(index % 2 == 1) Color.Transparent else cardBackgroundColor
                            Spacer(Modifier.height(25.dp).fillMaxWidth().background(color))
                        }
                    }
                }
                FullScrollBox(Modifier.fillMaxSize(), verticalStateList = verticalStateList) {
                    Column(Modifier.fillMaxSize()) {
                        files.value.forEachIndexed { index, _ ->
                            val fileCommit = files.value[index]
                            Row(Modifier
                                .height(25.dp)
                                .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                val resource = when(fileCommit.changeType) {
                                    DiffEntry.ChangeType.ADD -> { "images/diff/ic-add-file.svg" to "icon modification type add file" }
                                    DiffEntry.ChangeType.COPY -> { "images/diff/ic-copy-file.svg" to "icon modification type copy file" }
                                    DiffEntry.ChangeType.DELETE -> { "images/diff/ic-remove-file.svg" to "icon modification type remove file" }
                                    DiffEntry.ChangeType.MODIFY -> { "images/diff/ic-modify-file.svg" to "icon modification type modify file" }
                                    DiffEntry.ChangeType.RENAME -> { "images/diff/ic-rename-file.svg" to "icon modification type rename file" }
                                }
                                val resourceConflict = "images/diff/ic-conflict-file.svg" to "icon modification type conflict file"
                                Spacer(Modifier.size(10.dp))
                                Icon(
                                    painterResource(if(fileCommit.isConflict) resourceConflict.first else resource.first),
                                    contentDescription = resource.second,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = fileCommit.simpleName(),
                                    modifier = Modifier.padding(start = 10.dp),
                                    fontFamily = Fonts.roboto(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = itemRepositoryText,
                                    textAlign = TextAlign.Left
                                )
                            }
                        }
                    }
                }
                VerticalScrollBox(verticalStateList = verticalStateList) {
                    Column(Modifier.fillMaxSize()) {
                        files.value.forEachIndexed { index, _ ->
                            val state: ContextMenuState = remember { ContextMenuState() }
                            val menuContext = itemsContextMenu.map {
                                ContextMenuItem(it.first) {
                                    it.second.value = files.value[index]
                                }
                            }
                            ContextMenuArea(items = { menuContext } , state = state) {
                                SourceTooltip(files.value[index].name) {
                                    Spacer(Modifier
                                        .height(25.dp)
                                        .fillMaxWidth()
                                        .detectTapGesturesWithContextMenu(state = state,
                                            onTap = {
                                                onClick.value = files.value[index]
                                            },
                                            onDoubleTap = {
                                                onDoubleClick.value = files.value[index]
                                            }
                                        )
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