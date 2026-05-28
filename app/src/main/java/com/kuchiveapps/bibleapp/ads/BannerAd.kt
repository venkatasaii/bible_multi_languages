package com.kuchiveapps.bibleapp.ads

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun BannerAd(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val adWidthDp = configuration.screenWidthDp
    val adView = remember {
        AdView(context).apply {
            setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidthDp))
            adUnitId = AdIds.BANNER
            layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            loadAd(AdRequest.Builder().build())
        }
    }
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { adView }
    )
}
