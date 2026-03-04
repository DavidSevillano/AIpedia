package com.burixer85.aipedia.core.domain.repository

import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.flow.StateFlow

interface AdRepository {
    val adPool: StateFlow<List<NativeAd>>
    fun loadAds(count: Int)
    fun clearAds()
    fun refreshAds(count: Int)
}