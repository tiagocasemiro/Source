package br.com.source.view.dashboard.right.composes

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.source.model.util.emptyString
import br.com.source.view.common.*
import br.com.source.view.model.Change
import br.com.source.view.model.Diff
import br.com.source.view.model.Line
import org.eclipse.jgit.diff.DiffEntry

@Composable
fun OpenStashCompose(diffs: List<Diff>) {
    val stateList = rememberScrollState()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {
        Column(
            Modifier.fillMaxSize().verticalScroll(state = stateList),
        ) {
            diffs.forEach { diff ->
                FileChange(diff)
                Spacer(Modifier.height(1.dp).fillMaxWidth().background(itemRepositoryBackground))
                Spacer(Modifier.height(20.dp).fillMaxWidth())
            }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(
                scrollState = stateList
            )
        )
    }
}

@Composable
fun FileChange(diff: Diff) {
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
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = diff.fileName,
                modifier = Modifier.padding(start = 10.dp),
                fontFamily = Fonts.roboto(),
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                color = itemRepositoryText,
                textAlign = TextAlign.Left
            )
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
                text = "Change ${index + 1}: lines, from ${change.positionOfChanges.startNew} to ${change.positionOfChanges.startNew + change.positionOfChanges.totalNew - 1}",
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
                            is Line.Add -> Color(220,235,220)
                            is Line.Remove -> Color(235,220,220)
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
                                is Line.Add -> Color(0,150,0)
                                is Line.Remove -> Color(150,0,0)
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