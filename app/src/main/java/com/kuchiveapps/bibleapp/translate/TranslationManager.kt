package com.kuchiveapps.bibleapp.translate

import com.google.android.gms.tasks.Tasks
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class SupportedLanguage(val code: String, val displayName: String)

class TranslationManager {

    val languages: List<SupportedLanguage> = listOf(
        SupportedLanguage(TranslateLanguage.ENGLISH, "English"),
        SupportedLanguage(TranslateLanguage.SPANISH, "Spanish"),
        SupportedLanguage(TranslateLanguage.FRENCH, "French"),
        SupportedLanguage(TranslateLanguage.GERMAN, "German"),
        SupportedLanguage(TranslateLanguage.ITALIAN, "Italian"),
        SupportedLanguage(TranslateLanguage.PORTUGUESE, "Portuguese"),
        SupportedLanguage(TranslateLanguage.HINDI, "Hindi"),
        SupportedLanguage(TranslateLanguage.CHINESE, "Chinese"),
        SupportedLanguage(TranslateLanguage.JAPANESE, "Japanese"),
        SupportedLanguage(TranslateLanguage.KOREAN, "Korean"),
        SupportedLanguage(TranslateLanguage.ARABIC, "Arabic"),
        SupportedLanguage(TranslateLanguage.RUSSIAN, "Russian"),
        SupportedLanguage(TranslateLanguage.TELUGU, "Telugu"),
        SupportedLanguage(TranslateLanguage.TAMIL, "Tamil"),
    )

    private val translators = mutableMapOf<Pair<String, String>, Translator>()

    private fun translator(source: String, target: String): Translator {
        val key = source to target
        return translators.getOrPut(key) {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(source)
                .setTargetLanguage(target)
                .build()
            Translation.getClient(options)
        }
    }

    suspend fun ensureModel(source: String, target: String) =
        withContext(Dispatchers.IO) {
            if (source == target) return@withContext
            val t = translator(source, target)
            Tasks.await(t.downloadModelIfNeeded(DownloadConditions.Builder().build()))
        }

    suspend fun translate(text: String, source: String, target: String): String =
        withContext(Dispatchers.IO) {
            if (source == target) return@withContext text
            val t = translator(source, target)
            Tasks.await(t.translate(text))
        }

    fun close() {
        translators.values.forEach { it.close() }
        translators.clear()
    }
}
