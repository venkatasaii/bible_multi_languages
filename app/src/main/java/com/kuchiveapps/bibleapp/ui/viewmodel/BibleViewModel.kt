package com.kuchiveapps.bibleapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kuchiveapps.bibleapp.data.BibleRepository
import com.kuchiveapps.bibleapp.data.BibleVersion
import com.kuchiveapps.bibleapp.data.Book
import com.kuchiveapps.bibleapp.data.ChapterJson
import com.kuchiveapps.bibleapp.translate.SupportedLanguage
import com.kuchiveapps.bibleapp.translate.TranslationManager
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BooksState(
    val loading: Boolean = true,
    val books: List<Book> = emptyList(),
    val versions: List<BibleVersion> = emptyList(),
    val versionId: String = "kjv"
)

data class ReaderState(
    val loading: Boolean = true,
    val bookIndex: Int = 0,
    val bookName: String = "",
    val chapterNumber: Int = 1,
    val chapterCount: Int = 1,
    val totalBooks: Int = 66,
    val verses: List<VerseDisplay> = emptyList(),
    val sourceLanguage: String = "en",
    val targetLanguage: String = "en",
    val translating: Boolean = false,
    val downloadingModel: Boolean = false,
    val error: String? = null,
) {
    /** UI shows a loading placeholder when the user picked a non-source language
     *  but the per-verse translations haven't arrived yet. */
    val awaitingTranslation: Boolean
        get() = targetLanguage != sourceLanguage &&
                verses.isNotEmpty() &&
                verses.firstOrNull()?.translated == null
}

data class VerseDisplay(
    val number: Int,
    val original: String,
    val translated: String? = null
)

class BibleViewModel(
    private val repository: BibleRepository,
    private val translator: TranslationManager
) : ViewModel() {

    private val _books = MutableStateFlow(BooksState())
    val books: StateFlow<BooksState> = _books.asStateFlow()

    private val _reader = MutableStateFlow(ReaderState())
    val reader: StateFlow<ReaderState> = _reader.asStateFlow()

    val languages: List<SupportedLanguage> get() = translator.languages

    init {
        loadBooks()
    }

    private fun loadBooks() {
        viewModelScope.launch {
            val versions = repository.versions
            val list = repository.books(_books.value.versionId)
            _books.update { it.copy(loading = false, books = list, versions = versions) }
        }
    }

    fun setVersion(versionId: String) {
        if (versionId == _books.value.versionId) return
        _books.update { it.copy(versionId = versionId, loading = true) }
        viewModelScope.launch {
            val list = repository.books(versionId)
            _books.update { it.copy(loading = false, books = list) }
            // Refresh current chapter under the new version.
            val r = _reader.value
            if (r.verses.isNotEmpty()) openChapter(r.bookIndex, r.chapterNumber)
        }
    }

    fun openChapter(bookIndex: Int, chapterNumber: Int) {
        val books = _books.value.books
        if (books.isEmpty()) return
        val book = books[bookIndex]
        _reader.update {
            it.copy(
                loading = true,
                bookIndex = bookIndex,
                bookName = book.name,
                chapterNumber = chapterNumber,
                chapterCount = book.chapterCount,
                totalBooks = books.size,
                verses = emptyList(),
                sourceLanguage = currentSourceLanguage(),
                error = null,
            )
        }
        viewModelScope.launch {
            val ch: ChapterJson = repository.chapter(
                _books.value.versionId, bookIndex, chapterNumber
            )
            val verses = ch.verses.map { VerseDisplay(it.number, it.text) }
            _reader.update { it.copy(loading = false, verses = verses) }
            // Re-translate if a non-source language was active.
            if (_reader.value.targetLanguage != currentSourceLanguage()) {
                translateChapter(_reader.value.targetLanguage)
            }
        }
    }

    fun nextChapter() {
        val r = _reader.value
        if (r.chapterNumber < r.chapterCount) openChapter(r.bookIndex, r.chapterNumber + 1)
        else if (r.bookIndex < _books.value.books.lastIndex) openChapter(r.bookIndex + 1, 1)
    }

    fun previousChapter() {
        val r = _reader.value
        if (r.chapterNumber > 1) openChapter(r.bookIndex, r.chapterNumber - 1)
        else if (r.bookIndex > 0) {
            val prev = _books.value.books[r.bookIndex - 1]
            openChapter(r.bookIndex - 1, prev.chapterCount)
        }
    }

    private fun currentSourceLanguage(): String =
        repository.versions.first { it.id == _books.value.versionId }.sourceLanguage

    fun translateChapter(targetLang: String) {
        val source = currentSourceLanguage()
        if (targetLang == source) {
            // Switching back to the source language: clear cached translations so
            // the original text shows immediately.
            _reader.update { state ->
                state.copy(
                    targetLanguage = targetLang,
                    error = null,
                    verses = state.verses.map { it.copy(translated = null) },
                )
            }
            return
        }
        // Switching to a non-source language: clear translations FIRST so neither
        // the previous language nor the English original briefly shows under the
        // new language pill. The UI treats verses-with-null-translation as a
        // loading state whenever targetLanguage != source.
        _reader.update { state ->
            state.copy(
                targetLanguage = targetLang,
                error = null,
                verses = state.verses.map { it.copy(translated = null) },
                downloadingModel = true,
                translating = false,
            )
        }
        viewModelScope.launch {
            try {
                translator.ensureModel(source, targetLang)
                _reader.update { it.copy(downloadingModel = false, translating = true) }

                val current = _reader.value.verses
                val translated = coroutineScope {
                    current.map { v ->
                        async { v.copy(translated = translator.translate(v.original, source, targetLang)) }
                    }.awaitAll()
                }

                // Guard against the user navigating away mid-translation.
                if (_reader.value.targetLanguage == targetLang && _reader.value.verses.size == translated.size) {
                    _reader.update { it.copy(translating = false, verses = translated) }
                } else {
                    _reader.update { it.copy(translating = false) }
                }
            } catch (e: Exception) {
                _reader.update {
                    it.copy(
                        downloadingModel = false,
                        translating = false,
                        error = e.message ?: "Translation failed",
                    )
                }
            }
        }
    }

    override fun onCleared() {
        translator.close()
        super.onCleared()
    }

    class Factory(
        private val repository: BibleRepository,
        private val translator: TranslationManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            BibleViewModel(repository, translator) as T
    }
}
