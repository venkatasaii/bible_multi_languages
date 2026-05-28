package com.saiapps.bibleapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import com.saiapps.bibleapp.R
import com.saiapps.bibleapp.data.Book
import com.saiapps.bibleapp.data.Testament
import com.saiapps.bibleapp.ui.viewmodel.BibleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooksScreen(viewModel: BibleViewModel, onOpenBook: (Int) -> Unit) {
    val state by viewModel.books.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = { VersionDropdown(viewModel) }
            )
        }
    ) { padding ->
        if (state.loading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }
        val ot = state.books.filter { it.testament == Testament.OLD }
        val nt = state.books.filter { it.testament == Testament.NEW }
        LazyColumn(contentPadding = PaddingValues(vertical = 8.dp), modifier = Modifier.padding(padding)) {
            item { SectionHeader(stringResource(R.string.old_testament)) }
            items(ot, key = { it.index }) { BookRow(it, onOpenBook) }
            item { SectionHeader(stringResource(R.string.new_testament)) }
            items(nt, key = { it.index }) { BookRow(it, onOpenBook) }
        }
    }
}

@Composable
private fun VersionDropdown(viewModel: BibleViewModel) {
    val state by viewModel.books.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    val current = state.versions.firstOrNull { it.id == state.versionId }
    Box {
        TextButton(onClick = { expanded = true }) {
            Text(current?.id?.uppercase() ?: state.versionId.uppercase())
            Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            state.versions.forEach { v ->
                DropdownMenuItem(
                    text = { Text(v.displayName) },
                    onClick = {
                        expanded = false
                        viewModel.setVersion(v.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(label: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        )
        HorizontalDivider()
    }
}

@Composable
private fun BookRow(book: Book, onOpen: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen(book.index) }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(book.name, style = MaterialTheme.typography.bodyLarge)
        Text(
            "${book.chapterCount} ch",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
