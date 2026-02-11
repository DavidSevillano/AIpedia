package com.burixer85.aipedia.presentation.models

import kotlinx.serialization.Serializable

@Serializable
data class Ai(
    val id: String,
    val name: String,
    val description: String,
    val website: String,
    val price_model: String, // Temporal para testearlo
    val logo_url: String, // Temporal para testearlo
    val categories: List<Category>? = null // Temporal para testearlo
)


