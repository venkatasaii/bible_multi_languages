package com.kuchiveapps.bibleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kuchiveapps.bibleapp.ui.screens.BooksScreen
import com.kuchiveapps.bibleapp.ui.screens.ReaderScreen
import com.kuchiveapps.bibleapp.ui.theme.BibleTheme
import com.kuchiveapps.bibleapp.ui.viewmodel.BibleViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: BibleViewModel by viewModels {
        val app = application as BibleApp
        BibleViewModel.Factory(app.repository, app.translator)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BibleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val nav = rememberNavController()
                    NavHost(navController = nav, startDestination = "books") {
                        composable("books") {
                            BooksScreen(
                                viewModel = viewModel,
                                onOpenBook = { bookIndex ->
                                    viewModel.openChapter(bookIndex, 1)
                                    (application as BibleApp).ads.maybeShowInterstitial(this@MainActivity)
                                    nav.navigate("reader")
                                }
                            )
                        }
                        composable("reader") {
                            ReaderScreen(
                                viewModel = viewModel,
                                onBack = { nav.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
