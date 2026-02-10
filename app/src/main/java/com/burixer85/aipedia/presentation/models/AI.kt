package com.burixer85.aipedia.presentation.models

import java.util.UUID

data class AI(
    val id: UUID,
    val name: String,
    val description: String,
    val website: String,
    val priceModel: Double,
    val logoUrl: String,
    val categories: List<Category>
)


