package com.burixer85.aipedia.core.data.local.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(primaryKeys = ["aiId", "categoryId"], indices = [Index("categoryId")])

data class AiCategoryCrossRef(
    val aiId: String,
    val categoryId: String
)