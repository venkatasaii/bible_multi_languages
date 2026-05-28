package com.kuchiveapps.bibleapp.data

import kotlinx.serialization.Serializable

@Serializable
data class BibleJson(
    val version: String,
    val language: String,
    val books: List<BookJson>
)

@Serializable
data class BookJson(
    val name: String,
    val testament: String,
    val chapters: List<ChapterJson>
)

@Serializable
data class ChapterJson(
    val number: Int,
    val verses: List<VerseJson>
)

@Serializable
data class VerseJson(
    val number: Int,
    val text: String
)

data class BibleVersion(
    val id: String,
    val displayName: String,
    val sourceLanguage: String,
    val assetFile: String
)

data class Book(
    val index: Int,
    val name: String,
    val abbreviation: String,
    val testament: Testament,
    val chapterCount: Int
)

enum class Testament { OLD, NEW }
