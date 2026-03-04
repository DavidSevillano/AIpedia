package com.burixer85.aipedia.core.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import com.burixer85.aipedia.core.domain.model.Ai
import com.burixer85.aipedia.core.domain.model.Category
import com.burixer85.aipedia.core.domain.model.Feature

@Composable
fun Ai.localizedDescription(): String {
    val currentLocale = LocalConfiguration.current.locales.get(0).language
    return when (currentLocale) {
        "es" -> descriptionEs
        "en" -> descriptionEn
        else -> descriptionEn
    }
}

@Composable
fun Category.localizedName(): String {
    val currentLocale = LocalConfiguration.current.locales.get(0).language
    return when (currentLocale) {
        "es" -> nameEs
        "en" -> nameEn
        else -> nameEn
    }
}

@Composable
fun Feature.localizedName(): String {
    val currentLocale = LocalConfiguration.current.locales.get(0).language
    return when (currentLocale) {
        "es" -> nameEs
        "en" -> nameEn
        else -> nameEn
    }
}

@Composable
fun Feature.localizedDescription(): String {
    val currentLocale = LocalConfiguration.current.locales.get(0).language
    return when (currentLocale) {
        "es" -> descriptionEs
        "en" -> descriptionEn
        else -> descriptionEn
    }
}


