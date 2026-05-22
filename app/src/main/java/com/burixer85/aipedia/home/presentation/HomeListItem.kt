package com.burixer85.aipedia.home.presentation

import androidx.compose.runtime.Immutable
import com.burixer85.aipedia.core.domain.model.AiSummary
import com.google.android.gms.ads.nativead.NativeAd

sealed interface HomeListItem {
    @Immutable data class AiCard(val ai: AiSummary) : HomeListItem
    data class AdCard(val nativeAd: NativeAd) : HomeListItem
}
