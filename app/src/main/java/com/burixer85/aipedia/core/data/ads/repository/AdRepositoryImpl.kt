package com.burixer85.aipedia.core.data.ads.repository

import android.content.Context
import android.util.Log
import com.burixer85.aipedia.BuildConfig
import com.burixer85.aipedia.core.domain.repository.AdRepository
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : AdRepository {

    private val _adPool = MutableStateFlow<List<NativeAd>>(emptyList())

    override val adPool: StateFlow<List<NativeAd>> = _adPool.asStateFlow()

    override fun loadAds(count: Int) {
        clearAds()

        val adLoader = AdLoader.Builder(context, BuildConfig.NATIVE_AD_ID)
            .forNativeAd { ad ->
                _adPool.value += ad
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e("AdRepository", "Fallo al cargar: ${error.message} (Código: ${error.code})")
                }

                override fun onAdLoaded() {
                    Log.d("AdRepository", "¡Anuncio cargado con éxito!")
                }
            })
            .build()

        adLoader.loadAds(AdRequest.Builder().build(), count)
    }

    override fun clearAds() {
        _adPool.value.forEach { it.destroy() }
        _adPool.value = emptyList()
    }

    override fun refreshAds(count: Int) {
        // 1. Destruir lo anterior para liberar memoria
        clearAds()

        // 2. Cargar nuevos
        loadAds(count)
    }
}