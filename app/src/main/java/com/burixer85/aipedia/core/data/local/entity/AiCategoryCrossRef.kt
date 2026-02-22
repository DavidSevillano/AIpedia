package com.burixer85.aipedia.core.data.local.entity

import androidx.room.Entity

@Entity(primaryKeys = ["aiId", "categoryId"])
data class AiCategoryCrossRef(
    val aiId: String,
    val categoryId: String
)