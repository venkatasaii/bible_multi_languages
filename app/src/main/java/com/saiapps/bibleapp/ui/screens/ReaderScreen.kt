package com.saiapps.bibleapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saiapps.bibleapp.R
import com.saiapps.bibleapp.ui.viewmodel.BibleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(viewModel: BibleViewModel, onBack: () -> Unit) {
    val state by viewModel.reader.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(state.bookName, style = MaterialTheme.typography.titleMedium)
                        Text(
                            stringResource(R.string.chapter, state.chapterNumber),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = { LanguageDropdown(viewModel) }
            )
        },
        bottomBar = { ChapterNavBar(viewModel) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            if (state.translating) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            }
            state.error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            if (state.loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.verses, key = { it.number }) { v ->
                        Row(verticalAlignment = Alignment.Top) {
                            Text(
                                text = v.number.toString(),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 8.dp, top = 2.dp)
                            )
                            Column {
                                Text(
                                    text = v.original,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                v.translated?.let { t ->
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = t,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontStyle = FontStyle.Italic
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

@Composable
private fun LanguageDropdown(viewModel: BibleViewModel) {
    val state by viewModel.reader.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    val current = viewModel.languages.firstOrNull { it.code == state.targetLanguage }
    Box {
        TextButton(onClick = { expanded = true }) {
            Icon(Icons.Filled.Translate, contentDescription = stringResource(R.string.translate_to))
            Text("  ${current?.displayName ?: state.targetLanguage}")
            Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
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
private fun ChapterNavBar(viewModel: BibleViewModel) {
    val state by viewModel.reader.collectAsState()
    HorizontalDivider()
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = { viewModel.previousChapter() }) {
            Icon(Icons.Filled.ChevronLeft, contentDescription = null)
            Text(stringResource(R.string.previous))
        }
        Text(
            "${state.chapterNumber} / ${state.chapterCount}",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TextButton(onClick = { viewModel.nextChapter() }) {
            Text(stringResource(R.string.next))
            Icon(Icons.Filled.ChevronRight, contentDescription = null)
        }
    }
}
