package com.burixer85.aipedia.ratings.domain.model

data class Review(
    val id: String,
    val aiId: String,
    val userId: String,
    val displayName: String,
    val score: Int,
    val body: String?,
    val createdAt: String
)
