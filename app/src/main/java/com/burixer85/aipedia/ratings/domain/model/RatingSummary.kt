package com.burixer85.aipedia.ratings.domain.model

data class RatingSummary(
    val average: Float,
    val totalCount: Int,
    val distribution: Map<Int, Int>
)
