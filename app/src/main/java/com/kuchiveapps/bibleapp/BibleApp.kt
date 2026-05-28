package com.kuchiveapps.bibleapp

import android.app.Application
import com.kuchiveapps.bibleapp.ads.AdsManager
import com.kuchiveapps.bibleapp.data.BibleRepository
import com.kuchiveapps.bibleapp.translate.TranslationManager

class BibleApp : Application() {
    lateinit var repository: BibleRepository
        private set
    lateinit var translator: TranslationManager
        private set
    lateinit var ads: AdsManager
        private set

    override fun onCreate() {
        super.onCreate()
        repository = BibleRepository(this)
        translator = TranslationManager()
        ads = AdsManager(this).also { it.initialize() }
    }
}
