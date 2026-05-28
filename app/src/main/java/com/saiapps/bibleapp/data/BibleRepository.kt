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
                testament = if (b.testament.equals("OT", true) || b.testament.equals("old", true))
                    Testament.OLD else Testament.NEW,
                chapterCount = b.chapters.size
            )
        }
    }

    suspend fun chapter(versionId: String, bookIndex: Int, chapterNumber: Int): ChapterJson {
        val bible = load(versionId)
        val book = bible.books[bookIndex]
        return book.chapters.first { it.number == chapterNumber }
    }
}
