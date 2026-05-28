package com.saiapps.bibleapp.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saiapps.bibleapp.R
import com.saiapps.bibleapp.ui.theme.LoraFamily
import com.saiapps.bibleapp.ui.viewmodel.BibleViewModel
import com.saiapps.bibleapp.ui.viewmodel.VerseDisplay

@Composable
fun ReaderScreen(viewModel: BibleViewModel, onBack: () -> Unit) {
    val state by viewModel.reader.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(state.bookIndex, state.chapterNumber) {
        listState.scrollToItem(0)
    }

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(Modifier.fillMaxSize()) {
            Spacer(Modifier.windowInsetsPadding(WindowInsets.statusBars))
            TopBar(viewModel, onBack)
            if (state.downloadingModel || state.translating) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = stringResource(
                            if (state.downloadingModel) R.string.downloading_language
                            else R.string.translating
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            state.error?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }
            Box(Modifier.weight(1f)) {
                if (state.loading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    AnimatedContent(
                        targetState = state.bookIndex to state.chapterNumber,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "chapter"
                    ) { _ ->
                        LazyColumn(
                            state = listState,
                            contentPadding = PaddingValues(horizontal = 26.dp, vertical = 12.dp)
                        ) {
                            item { ChapterHeader(state.bookName, state.chapterNumber) }
                            item {
                                val useTranslated = state.verses.firstOrNull()?.translated != null
                                VerseParagraph(state.verses, useTranslated = useTranslated)
                            }
                            item { Spacer(Modifier.height(32.dp)) }
                        }
                    }
                }
            }
            BottomNav(viewModel)
            Spacer(Modifier.windowInsetsPadding(WindowInsets.navigationBars))
        }
    }
}

@Composable
private fun TopBar(viewModel: BibleViewModel, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        Spacer(Modifier.weight(1f))
        LanguagePill(viewModel)
        Spacer(Modifier.width(8.dp))
    }
}

@Composable
private fun LanguagePill(viewModel: BibleViewModel) {
    val state by viewModel.reader.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    val current = viewModel.languages.firstOrNull { it.code == state.targetLanguage }
    Box {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.clickable { expanded = true }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Icon(
                    Icons.Filled.Translate,
                    contentDescription = stringResource(R.string.translate_to),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = current?.displayName ?: state.targetLanguage,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp).padding(start = 2.dp)
                )
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            viewModel.languages.forEach { lang ->
                DropdownMenuItem(
                    text = { Text(lang.displayName) },
                    onClick = {
                        expanded = false
                        viewModel.translateChapter(lang.code)
                    }
                )
            }
        }
    }
}

@Composable
private fun ChapterHeader(bookName: String, chapterNumber: Int) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = bookName.uppercase(),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(
                modifier = Modifier.width(40.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
            Box(
                Modifier
                    .padding(horizontal = 10.dp)
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
            HorizontalDivider(
                modifier = Modifier.width(40.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.chapter, chapterNumber),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun VerseParagraph(verses: List<VerseDisplay>, useTranslated: Boolean) {
    if (verses.isEmpty()) return
    val primary = MaterialTheme.colorScheme.primary
    val text = remember(verses, useTranslated) {
        buildAnnotatedString {
            verses.forEachIndexed { idx, v ->
                val body = if (useTranslated) (v.translated ?: v.original) else v.original
                if (idx == 0) {
                    val first = body.firstOrNull()?.toString() ?: ""
                    val rest = if (body.isNotEmpty()) body.substring(1) else ""
                    withStyle(
                        SpanStyle(
                            color = primary,
                            fontSize = 56.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = LoraFamily
                        )
                    ) { append(first) }
                    append(rest)
                } else {
                    append(' ')
                    withStyle(
                        SpanStyle(
                            color = primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            baselineShift = BaselineShift.Superscript
                        )
                    ) { append(" ${v.number} ") }
                    append(body)
                }
            }
        }
    }
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onBackground
        )
    )
}

@Composable
private fun BottomNav(viewModel: BibleViewModel) {
    val state by viewModel.reader.collectAsState()
    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val hasPrevious = state.chapterNumber > 1 || state.bookIndex > 0
        val hasNext = state.chapterNumber < state.chapterCount ||
                (state.bookIndex >= 0 && state.bookIndex < state.totalBooks - 1)
        NavButton(
            label = stringResource(R.string.previous),
            iconLeading = true,
            enabled = hasPrevious && !state.loading,
            onClick = { viewModel.previousChapter() }
        )
        Text(
            text = "${state.chapterNumber} / ${state.chapterCount}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
        NavButton(
            label = stringResource(R.string.next),
            iconLeading = false,
            enabled = hasNext && !state.loading,
            onClick = { viewModel.nextChapter() }
        )
    }
}

@Composable
private fun NavButton(label: String, iconLeading: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(20.dp),
        color = if (enabled) MaterialTheme.colorScheme.surfaceVariant
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            if (iconLeading) {
                Icon(
                    Icons.Filled.ChevronLeft,
                    contentDescription = null,
                    tint = if (enabled) MaterialTheme.colorScheme.primary
                           else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(2.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (enabled) MaterialTheme.colorScheme.onBackground
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            if (!iconLeading) {
                Spacer(Modifier.width(2.dp))
                Icon(
                    Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = if (enabled) MaterialTheme.colorScheme.primary
                           else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
