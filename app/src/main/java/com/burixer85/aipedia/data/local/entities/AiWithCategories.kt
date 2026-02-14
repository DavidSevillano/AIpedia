package com.burixer85.aipedia.data.local.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class AiWithCategories(
    @Embedded val ai: AiEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            AiCategoryCrossRef::class,
            parentColumn = "aiId",
            entityColumn = "categoryId"
        )
    )
    val categories: List<CategoryEntity>
)