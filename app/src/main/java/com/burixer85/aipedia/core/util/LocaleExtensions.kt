package com.burixer85.aipedia.core.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import com.burixer85.aipedia.core.domain.model.Ai
import com.burixer85.aipedia.core.domain.model.AiSpec
import com.burixer85.aipedia.core.domain.model.AiUseCase
import com.burixer85.aipedia.core.domain.model.Category
import com.burixer85.aipedia.core.domain.model.Feature
import com.burixer85.aipedia.core.domain.model.Platform
import com.burixer85.aipedia.core.domain.model.PricingPlan
import com.burixer85.aipedia.core.domain.model.ProCon

@Composable
private fun isSpanish(): Boolean =
    LocalConfiguration.current.locales.get(0).language == "es"

@Composable
fun Ai.localizedDescription(): String =
    if (isSpanish()) descriptionEs else descriptionEn

@Composable
fun Ai.localizedTagline(): String? =
    if (isSpanish()) taglineEs else taglineEn

@Composable
fun Category.localizedName(): String =
    if (isSpanish()) nameEs else nameEn

@Composable
fun Feature.localizedName(): String =
    if (isSpanish()) nameEs else nameEn

@Composable
fun Feature.localizedDescription(): String =
    if (isSpanish()) descriptionEs.orEmpty() else descriptionEn.orEmpty()

@Composable
fun Platform.localizedName(): String =
    if (isSpanish()) nameEs ?: name else nameEn ?: name

@Composable
fun AiSpec.localizedLabel(): String =
    if (isSpanish()) labelEs else labelEn

@Composable
fun AiSpec.localizedValue(): String =
    if (isSpanish()) valueEs else valueEn

@Composable
fun PricingPlan.localizedName(): String =
    if (isSpanish()) nameEs else nameEn

@Composable
fun PricingPlan.localizedBillingNote(): String? =
    if (isSpanish()) billingNoteEs else billingNoteEn

@Composable
fun PricingPlan.localizedIncludes(): String? =
    if (isSpanish()) includesEs else includesEn

@Composable
fun ProCon.localizedText(): String =
    if (isSpanish()) textEs else textEn

@Composable
fun AiUseCase.localizedName(): String =
    if (isSpanish()) nameEs else nameEn
