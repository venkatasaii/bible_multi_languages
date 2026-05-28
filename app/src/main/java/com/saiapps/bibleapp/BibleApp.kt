package com.saiapps.bibleapp

import android.app.Application
import com.saiapps.bibleapp.data.BibleRepository
import com.saiapps.bibleapp.translate.TranslationManager

class BibleApp : Application() {
    lateinit var repository: BibleRepository
        private set
    lateinit var translator: TranslationManager
        private set

    override fun onCreate() {
        super.onCreate()
        repository = BibleRepository(this)
        translator = TranslationManager()
    }
}
