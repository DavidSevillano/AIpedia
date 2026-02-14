package com.burixer85.aipedia.data.local.entities

import androidx.room.Entity

@Entity(primaryKeys = ["aiId", "categoryId"])
data class AiCategoryCrossRef(
    val aiId: String,
    val categoryId: String
)