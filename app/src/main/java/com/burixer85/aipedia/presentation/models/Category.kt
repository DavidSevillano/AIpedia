package com.burixer85.aipedia.presentation.models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Category(
    val id: String,
    val name: String
)