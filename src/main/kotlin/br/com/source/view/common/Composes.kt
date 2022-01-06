package br.com.source.view.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import br.com.source.model.util.Message
import br.com.source.model.util.conditional
import br.com.source.model.util.detectTapGesturesWithContextMenu
import br.com.source.model.util.emptyString
import br.com.source.view.components.SourceNotification
import br.com.source.view.components.TypeCommunication
import br.com.source.view.components.showError
import br.com.source.view.components.showWarn
import br.com.source.view.model.Change
import br.com.source.view.model.Diff
import br.com.source.view.model.FileCommit
import br.com.source.view.model.Line
import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.stage.DirectoryChooser
import kotlinx.coroutines.*
import org.eclipse.jgit.diff.DiffEntry
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.SplitPaneScope
import java.awt.Cursor
import java.io.File
import java.util.*
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

@Composable
fun LoadState(showLoad: State<Boolean>, content: @Composable () -> Unit) {
    if(showLoad.value) {
        showLoad()
    } else {
        hideLoad()
    }
    content()
}

@Composable
fun <T>MessageCompose(messageState: Message<T>, content: @Composable (T) -> Unit) {
    when(messageState) {
        is Message.Error -> {
            showError(messageState)
        }
        is Message.Warn -> {
            showWarn(messageState)
        }
        is Message.Success -> {
            content(messageState.obj)
        }
    }
}

fun hideLoad() {
    loadState.value = false
}

@Composable
fun Load(content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        content()
        LoadCompose()
    }
}

@Composable
private fun LoadCompose() {
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

private val notificationList = mutableStateOf<MutableList<NotificationData>>(mutableListOf())

data class NotificationData(
    val message: String,
    val type: TypeCommunication = TypeCommunication.none,
    val uuid: String = UUID.randomUUID().toString()
)

fun showNotification(message: String, type: TypeCommunication = TypeCommunication.none) {
    val list = mutableListOf<NotificationData>()
    list.addAll(notificationList.value)
    list.add(NotificationData(message, type))
    notificationList.value = list
}

fun showSuccessNotification(message: String) = showNotification(message, TypeCommunication.success)

fun showWarnNotification(message: String) = showNotification(message, TypeCommunication.warn)

@ExperimentalComposeUiApi
@Composable
fun CreateNotification(content: @Composable () -> Unit) {
    val deletedItem = remember { mutableStateListOf<NotificationData>() }
    val duration = 5000L
    Box {
        content()
        if(notificationList.value.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Top
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End,
                ) {
                    itemsIndexed(
                        items = notificationList.value,
                        itemContent = { _, item ->
                            AnimatedVisibility(
                                visible = !deletedItem.contains(item),
                                enter = expandVertically(),
                                exit = shrinkVertically(animationSpec = tween(durationMillis = 1000))
                            ) {
                                Card(
                                    modifier = Modifier.heightIn(min = 80.dp).widthIn(min = 300.dp, max = 600.dp).padding(10.dp).background(Color.White),
                                    elevation = 0.dp,
                                    shape = RoundedCornerShape(5.dp)
                                ) {
                                    SourceNotification(item) {
                                        deletedItem.add(item)
                                    }
                                    CoroutineScope(Job()).launch((Dispatchers.IO)) {
                                        delay(duration)
                                        deletedItem.add(item)
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SourceTooltip(message: String, content: @Composable () -> Unit) {
    TooltipArea(
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
        modifier = Modifier.padding(start = 4.dp),
        delayMillis = 600, // in milliseconds
        tooltipPlacement = TooltipPlacement.CursorPoint(
            alignment = Alignment.BottomEnd,
            offset = DpOffset(10.dp, 0.dp)
        ),
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
            key(change.hashCode()) {
                ChangeCompose(change = change, index = index)
            }
        }
    }
}

@Composable
fun ChangeCompose(change: Change, index: Int) {
    val listState = rememberScrollState()
    Column {
        Spacer(Modifier.height(1.dp).fillMaxWidth().background(itemRepositoryBackground))
        Column(
            Modifier.background(dialogBackgroundColor).fillMaxWidth().height(25.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = change.changePosition,
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
                    key(line.hashCode()) {
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
            }
            Box {
                Column(
                    Modifier.fillMaxWidth()
                ) {
                    change.lines.forEach { line ->
                        key(line.hashCode()) {
                            val background = when(line) {
                                is Line.Add -> Color(230,245,230)
                                is Line.Remove -> Color(245,230,230)
                                else -> Color.Transparent
                            }
                            Spacer(Modifier.height(25.dp).background(background).fillMaxWidth().absolutePadding(left = 52.dp))
                        }
                    }
                }
                Column(
                    Modifier.fillMaxWidth().horizontalScroll(listState).fillMaxWidth()
                ) {
                    change.lines.forEach { line ->
                        key(line.hashCode()) {
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
                }
                HorizontalScrollbar(
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(vertical = paddingScrollBar),
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
    BoxWithConstraints {
        val maxWidth = this.maxWidth
        val maxHeight = this.maxHeight
        val minWidth = this.minWidth
        val minHeight = this.minHeight
        Box(Modifier.fillMaxSize()) {
            Box(modifier
                .verticalScroll(verticalStateList)
                .horizontalScroll(horizontalStateList)
                .widthIn(min = minWidth, max = maxWidth)
                .heightIn(min = minHeight, max = maxHeight)) {
                content()
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().padding(horizontal = paddingScrollBar),
                adapter = rememberScrollbarAdapter(
                    scrollState = verticalStateList
                )
            )
            HorizontalScrollbar(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(vertical = paddingScrollBar),
                adapter = rememberScrollbarAdapter(
                    scrollState = horizontalStateList
                )
            )
        }
    }
}

@Composable
fun VerticalScrollBox(modifier: Modifier = Modifier, verticalStateList: ScrollState = rememberScrollState(), content: @Composable () -> Unit) {
    BoxWithConstraints {
        val maxWidth = this.maxWidth
        val maxHeight = this.maxHeight
        val minWidth = this.minWidth
        val minHeight = this.minHeight
        Box(Modifier.fillMaxSize()) {
            Box(
                modifier
                    .verticalScroll(verticalStateList)
                    .widthIn(min = minWidth, max = maxWidth)
                    .heightIn(min = minHeight, max = maxHeight)
            ) {
                content()
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().padding(horizontal = paddingScrollBar),
                adapter = rememberScrollbarAdapter(
                    scrollState = verticalStateList
                )
            )
        }
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
internal fun FilesChangedCompose(title: String? = null, files: List<FileCommit>, onClick: (FileCommit) -> Unit = {}, onDoubleClick: (FileCommit) -> Unit = {}, itemsContextMenu: List<Pair<String, (FileCommit) -> Unit>> = emptyList()) {
    val state: ContextMenuState = remember { ContextMenuState() }
    val verticalStateList: ScrollState = rememberScrollState()
    Column(modifier = Modifier.fillMaxSize()) {
        if(title != null && title.trim().isNotEmpty()) {
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
            }
            HorizontalDivider()
        }
        EmptyStateItem(files.isNotEmpty()) {
            Box {
                VerticalScrollBox(verticalStateList = verticalStateList) {
                    Column(Modifier.fillMaxSize()) {
                        files.forEachIndexed { index, _ ->
                            val color = if(index % 2 == 0) Color.Transparent else cardBackgroundColor
                            Spacer(Modifier.height(25.dp).fillMaxWidth().background(color))
                        }
                    }
                }
                FullScrollBox(Modifier.fillMaxSize(), verticalStateList = verticalStateList) {
                    Column(Modifier.fillMaxSize()) {
                        files.forEach { fileCommit ->
                            key(fileCommit.hashCode()) {
                                Row(
                                    Modifier
                                        .height(25.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    val resource = when (fileCommit.changeType) {
                                        DiffEntry.ChangeType.ADD -> {
                                            "images/diff/ic-add-file.svg" to "icon modification type add file"
                                        }
                                        DiffEntry.ChangeType.COPY -> {
                                            "images/diff/ic-copy-file.svg" to "icon modification type copy file"
                                        }
                                        DiffEntry.ChangeType.DELETE -> {
                                            "images/diff/ic-remove-file.svg" to "icon modification type remove file"
                                        }
                                        DiffEntry.ChangeType.MODIFY -> {
                                            "images/diff/ic-modify-file.svg" to "icon modification type modify file"
                                        }
                                        DiffEntry.ChangeType.RENAME -> {
                                            "images/diff/ic-rename-file.svg" to "icon modification type rename file"
                                        }
                                    }
                                    val resourceConflict =
                                        "images/diff/ic-conflict-file.svg" to "icon modification type conflict file"
                                    Spacer(Modifier.size(10.dp))
                                    Icon(
                                        painterResource(if (fileCommit.isConflict) resourceConflict.first else resource.first),
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
                }
                VerticalScrollBox(verticalStateList = verticalStateList) {
                    Column(Modifier.fillMaxSize()) {
                        files.forEach { fileCommit ->
                            key(fileCommit.hashCode()) {
                                val menuContext = itemsContextMenu.map {
                                    ContextMenuItem(it.first) {
                                        it.second(fileCommit)
                                    }
                                }
                                ContextMenuArea(items = { menuContext }, state = state) {
                                    SourceTooltip(fileCommit.name) {
                                        Spacer(Modifier
                                            .height(25.dp)
                                            .fillMaxWidth()
                                            .detectTapGesturesWithContextMenu(state = state,
                                                onTap = {
                                                    onClick(fileCommit)
                                                },
                                                onDoubleTap = {
                                                    onDoubleClick(fileCommit)
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
}

@Composable
fun EmptyStateItem(canShowContent: Boolean, message: String = "Empty", content: @Composable () -> Unit) {
    if(canShowContent.not()) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = message,
                fontFamily = Fonts.roboto(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = itemRepositoryText,
            )
        }
    } else {
        content()
    }
}

@Composable
fun <T>EmptyStateOnNullItem(t: T?, message: String = "Empty", content: @Composable (T) -> Unit) {
    if(t == null) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = message,
                fontFamily = Fonts.roboto(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = itemRepositoryText,
            )
        }
    } else {
        content(t)
    }
}

@Composable
fun SegmentedControl(state: MutableState<Int>, firstLabel: String, secondLabel: String, onFirst: () -> Unit, onSecond: () -> Unit) {
    Row(Modifier.height(24.dp)) {
        Box(
            modifier = Modifier
                .conditional(
                    condition = state.value == 0,
                    ifTrue = {
                        it.background(colorSelectedSegmentedControl, RoundedCornerShape(5.dp, 0.dp, 0.dp, 5.dp))
                    },
                    ifFalse = {
                        it.background(colorUnselectedSegmentedControl, RoundedCornerShape(5.dp, 0.dp, 0.dp, 5.dp))
                    }
                )
                .height(24.dp)
                .width(100.dp)
                .clickable {
                    if(state.value == 1) {
                        state.value = 0
                        onFirst()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(firstLabel,
                color = if(state.value == 0) Color.White else itemRepositoryText,
                fontFamily = Fonts.roboto(),
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp
            )
        }
        Spacer(Modifier.width(1.dp).fillMaxHeight())
        Box(
            modifier = Modifier
                .conditional(
                    condition = state.value == 1,
                    ifTrue = {
                        it.background(colorSelectedSegmentedControl, RoundedCornerShape(0.dp, 5.dp, 5.dp, 0.dp))
                    },
                    ifFalse = {
                        it.background(colorUnselectedSegmentedControl, RoundedCornerShape(0.dp, 5.dp, 5.dp, 0.dp))
                    }
                )
                .height(24.dp)
                .width(100.dp)
                .clickable {
                    if(state.value == 0) {
                        state.value = 1
                        onSecond()
                    }
                },
                contentAlignment = Alignment.Center
        ) {
            Text(secondLabel,
                color = if(state.value == 1) Color.White else itemRepositoryText,
                fontFamily = Fonts.roboto(),
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp
            )
        }
    }
}
