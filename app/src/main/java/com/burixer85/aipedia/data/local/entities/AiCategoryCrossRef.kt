package com.burixer85.aipedia.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "ai_category_cross_ref",
    primaryKeys = ["aiId", "categoryId"]
)
data class AiCategoryCrossRef(
    @ColumnInfo(name = "ai_id")
    val aiId: String,
    @ColumnInfo(name = "category_id")
    val categoryId: String
)