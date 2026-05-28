package com.saiapps.bibleapp.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class BibleRepository(private val context: Context) {

    private val cache = mutableMapOf<String, BibleJson>()
    private val mutex = Mutex()
    private val json = Json { ignoreUnknownKeys = true }

    val versions: List<BibleVersion> = listOf(
        BibleVersion(
            id = "kjv",
            displayName = "King James Version (KJV)",
            sourceLanguage = "en",
            assetFile = "kjv.json"
        )
    )

    suspend fun load(versionId: String): BibleJson = mutex.withLock {
        cache[versionId]?.let { return it }
        val version = versions.first { it.id == versionId }
        val text = withContext(Dispatchers.IO) {
            context.assets.open(version.assetFile).bufferedReader().use { it.readText() }
        }
        val parsed = json.decodeFromString(BibleJson.serializer(), text)
        cache[versionId] = parsed
        parsed
    }

    suspend fun books(versionId: String): List<Book> {
        val bible = load(versionId)
        return bible.books.mapIndexed { idx, b ->
            Book(
                index = idx,
                name = b.name,
                abbreviation = BOOK_ABBREVIATIONS.getOrElse(idx) { b.name.take(3) },
                testament = if (b.testament.equals("OT", true) || b.testament.equals("old", true))
                    Testament.OLD else Testament.NEW,
                chapterCount = b.chapters.size
            )
        }
    }

    companion object {
        private val BOOK_ABBREVIATIONS = listOf(
            // Old Testament (39)
            "Gen", "Ex", "Lev", "Num", "Deut", "Josh", "Judg", "Ruth",
            "1 Sa", "2 Sa", "1 Ki", "2 Ki", "1 Ch", "2 Ch", "Ezra", "Neh",
            "Esth", "Job", "Ps", "Prov", "Eccl", "Song", "Isa", "Jer",
            "Lam", "Ezek", "Dan", "Hos", "Joel", "Amos", "Obad", "Jonah",
            "Mic", "Nah", "Hab", "Zeph", "Hag", "Zech", "Mal",
            // New Testament (27)
            "Matt", "Mark", "Luke", "John", "Acts", "Rom", "1 Co", "2 Co",
            "Gal", "Eph", "Phil", "Col", "1 Th", "2 Th", "1 Ti", "2 Ti",
            "Titus", "Phlm", "Heb", "Jas", "1 Pe", "2 Pe", "1 Jn", "2 Jn",
            "3 Jn", "Jude", "Rev"
        )
    }

    suspend fun chapter(versionId: String, bookIndex: Int, chapterNumber: Int): ChapterJson {
        val bible = load(versionId)
        val book = bible.books[bookIndex]
        return book.chapters.first { it.number == chapterNumber }
    }
}
