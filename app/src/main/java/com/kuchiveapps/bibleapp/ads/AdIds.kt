package com.kuchiveapps.bibleapp.ads

/**
 * Centralised AdMob unit IDs. We ship Google's official TEST IDs in source so the
 * dev/closed-test builds never serve real ads or count fraudulent impressions.
 * Swap each value with your real AdMob unit ID immediately before the Production
 * release. The AndroidManifest APPLICATION_ID also needs the same swap.
 *
 * Real values will come from https://apps.admob.com/ once the AdMob account is set up.
 */
object AdIds {
    const val BANNER = "ca-app-pub-3940256099942544/6300978111"
    const val INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712"
}
