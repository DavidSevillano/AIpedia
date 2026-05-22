package com.burixer85.aipedia.core.domain.model

data class AiSpec(
    val labelEs: String,
    val labelEn: String,
    val valueEs: String,
    val valueEn: String,
)

data class PricingPlan(
    val nameEs: String,
    val nameEn: String,
    val priceMonthly: Double,
    val priceYearly: Double?,
    val billingNoteEs: String?,
    val billingNoteEn: String?,
    val includesEs: String?,
    val includesEn: String?,
)

data class ProCon(
    val isPro: Boolean,
    val textEs: String,
    val textEn: String,
)

data class Integration(
    val name: String,
    val iconUrl: String?,
)

data class AiUseCase(
    val nameEs: String,
    val nameEn: String,
)

data class ComparisonData(
    val specs: List<AiSpec> = emptyList(),
    val plans: List<PricingPlan> = emptyList(),
    val pros: List<ProCon> = emptyList(),
    val cons: List<ProCon> = emptyList(),
    val integrations: List<Integration> = emptyList(),
    val useCases: List<AiUseCase> = emptyList(),
)
