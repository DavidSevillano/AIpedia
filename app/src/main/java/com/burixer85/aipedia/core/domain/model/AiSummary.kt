package com.burixer85.aipedia.core.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class AiSummary(
    val id: String,
    val name: String,
    val priceModel: String,
    val logo: String?,
    val categoryName: String?
)
