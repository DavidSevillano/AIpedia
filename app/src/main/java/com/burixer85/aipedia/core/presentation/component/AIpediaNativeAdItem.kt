package com.burixer85.aipedia.core.presentation.component

import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import com.burixer85.aipedia.R

@Composable
fun AIpediaNativeAdItem(
    nativeAd: NativeAd,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        factory = { ctx ->
            val view = LayoutInflater.from(ctx).inflate(R.layout.native_ad_layout, null) as NativeAdView

            populateNativeAdView(nativeAd, view)

            view
        },
        update = { view ->
            populateNativeAdView(nativeAd, view)
        }
    )
}

fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
    adView.headlineView = adView.findViewById(R.id.ad_headline)
    adView.bodyView = adView.findViewById(R.id.ad_body)
    adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
    adView.mediaView = adView.findViewById(R.id.ad_media)
    adView.adChoicesView = adView.findViewById(R.id.ad_choices)

    (adView.headlineView as? TextView)?.text = nativeAd.headline
    (adView.bodyView as? TextView)?.text = nativeAd.body

    if (nativeAd.callToAction == null) {
        adView.callToActionView?.visibility = android.view.View.INVISIBLE
    } else {
        adView.callToActionView?.visibility = android.view.View.VISIBLE
        (adView.callToActionView as Button).text = nativeAd.callToAction
    }

    if (nativeAd.icon != null) {
        (adView.iconView as? ImageView)?.setImageDrawable(nativeAd.icon?.drawable)
        adView.iconView?.visibility = android.view.View.VISIBLE
    } else {
        adView.iconView?.visibility = android.view.View.GONE
    }

    if (nativeAd.mediaContent != null) {
        adView.mediaView?.visibility = android.view.View.VISIBLE
        adView.mediaView?.mediaContent = nativeAd.mediaContent!!
    } else {
        adView.mediaView?.visibility = android.view.View.GONE
    }

    adView.setNativeAd(nativeAd)
}