package com.burixer85.aipedia.presentation.model

import kotlinx.serialization.Serializable

@Serializable
data class Feature(
    val id: String,
    val name: String,
    val description: String,
    val icon: String
)
