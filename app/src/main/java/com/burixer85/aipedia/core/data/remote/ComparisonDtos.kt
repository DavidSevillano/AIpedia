package com.burixer85.aipedia.core.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AiComparisonDto(
    val id: String,
    @SerialName("ai_specs") val specs: List<SpecDto> = emptyList(),
    @SerialName("pricing_plans") val plans: List<PricingPlanDto> = emptyList(),
    @SerialName("ai_pros_cons") val prosCons: List<ProConDto> = emptyList(),
    val integrations: List<IntegrationDto> = emptyList(),
    @SerialName("use_cases") val useCases: List<UseCaseDto> = emptyList(),
)

@Serializable
data class SpecDto(
    @SerialName("label_es") val labelEs: String,
    @SerialName("label_en") val labelEn: String,
    @SerialName("value_es") val valueEs: String,
    @SerialName("value_en") val valueEn: String,
    @SerialName("sort_order") val sortOrder: Int = 0,
)

@Serializable
data class PricingPlanDto(
    @SerialName("name_es") val nameEs: String,
    @SerialName("name_en") val nameEn: String,
    @SerialName("price_monthly") val priceMonthly: Double = 0.0,
    @SerialName("price_yearly") val priceYearly: Double? = null,
    @SerialName("billing_note_es") val billingNoteEs: String? = null,
    @SerialName("billing_note_en") val billingNoteEn: String? = null,
    @SerialName("includes_es") val includesEs: String? = null,
    @SerialName("includes_en") val includesEn: String? = null,
    @SerialName("sort_order") val sortOrder: Int = 0,
)

@Serializable
data class ProConDto(
    val kind: String,
    @SerialName("text_es") val textEs: String,
    @SerialName("text_en") val textEn: String,
    @SerialName("sort_order") val sortOrder: Int = 0,
)

@Serializable
data class IntegrationDto(
    val name: String,
    @SerialName("icon_url") val iconUrl: String? = null,
)

@Serializable
data class UseCaseDto(
    @SerialName("name_es") val nameEs: String,
    @SerialName("name_en") val nameEn: String,
)
