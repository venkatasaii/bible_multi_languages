package com.kuchiveapps.bibleapp.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Owns the AdMob lifecycle for the app.
 *
 * Policy (chosen by the user, see [docs/PLAY_STORE.md]):
 *  - A banner is allowed on the Books (home) screen only — never inside the reader.
 *  - One interstitial per app session, shown when the user opens their first chapter.
 *    Bible-app users review-bomb apps that show ads mid-reading, so we are intentionally
 *    light here. Revenue tradeoff is accepted.
 */
class AdsManager(private val appContext: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var initialized = false
    private var interstitial: InterstitialAd? = null
    private var interstitialShownThisSession = false

    /**
     * Toggleable kill switch. When the Remove-Ads IAP lands this becomes a DataStore flag.
     */
    var adsEnabled: Boolean = true

    fun initialize() {
        if (initialized) return
        initialized = true
        MobileAds.initialize(appContext) { /* status callback unused */ }
        preloadInterstitial()
    }

    private fun preloadInterstitial() {
        if (!adsEnabled || interstitial != null) return
        scope.launch {
            InterstitialAd.load(
                appContext,
                AdIds.INTERSTITIAL,
                AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitial = ad
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        interstitial = null
                    }
                }
            )
        }
    }

    /**
     * Show the interstitial once per session if it's ready. Caller passes an Activity
     * because AdMob requires a foreground activity to render full-screen content.
     */
    fun maybeShowInterstitial(activity: Activity) {
        if (!adsEnabled || interstitialShownThisSession) return
        val ad = interstitial ?: return
        interstitialShownThisSession = true
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitial = null
                preloadInterstitial()
            }
            override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                interstitial = null
                preloadInterstitial()
            }
        }
        ad.show(activity)
    }
}
