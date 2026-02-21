package com.burixer85.aipedia.presentation.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: String,
    val name: String,
    val icon: String
)