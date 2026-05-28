package com.saiapps.bibleapp.ui.screens

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
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.saiapps.bibleapp.R
import com.saiapps.bibleapp.data.Book
import com.saiapps.bibleapp.data.Testament
import com.saiapps.bibleapp.ui.viewmodel.BibleViewModel

@Composable
fun BooksScreen(viewModel: BibleViewModel, onOpenBook: (Int) -> Unit) {
    val state by viewModel.books.collectAsState()

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        if (state.loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            return
        }
        val ot = state.books.filter { it.testament == Testament.OLD }
        val nt = state.books.filter { it.testament == Testament.NEW }
        val insets = WindowInsets.statusBars.union(WindowInsets.navigationBars).asPaddingValues()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = insets.calculateTopPadding(),
                bottom = insets.calculateBottomPadding() + 24.dp
            )
        ) {
            item { Hero(viewModel) }
            item { SectionTitle(stringResource(R.string.old_testament)) }
            items(ot, key = { it.index }) { BookRow(it, onOpenBook) }
            item { Spacer(Modifier.height(24.dp)) }
            item { SectionTitle(stringResource(R.string.new_testament)) }
            items(nt, key = { it.index }) { BookRow(it, onOpenBook) }
        }
    }
}

@Composable
private fun Hero(viewModel: BibleViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 24.dp, start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.app_name).uppercase(),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.tagline),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        Ornament()
        Spacer(Modifier.height(20.dp))
        VersionPill(viewModel)
    }
}

@Composable
private fun Ornament() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        HorizontalDivider(
            modifier = Modifier.width(80.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
        )
        Box(
            Modifier
                .padding(horizontal = 10.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )
        HorizontalDivider(
            modifier = Modifier.width(80.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
        )
    }
}

@Composable
private fun VersionPill(viewModel: BibleViewModel) {
    val state by viewModel.books.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    val current = state.versions.firstOrNull { it.id == state.versionId }
    Box {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.clickable { expanded = true }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = current?.id?.uppercase() ?: state.versionId.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
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
private fun SectionTitle(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(12.dp))
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun BookRow(book: Book, onOpen: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen(book.index) }
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = book.abbreviation,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = book.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "${book.chapterCount} chapters",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
