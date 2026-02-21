package com.burixer85.aipedia.data.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.burixer85.aipedia.domain.model.Ai


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
    val categories: List<CategoryEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(AiFeatureCrossRef::class, parentColumn = "aiId", entityColumn = "featureId")
    )
    val features: List<FeatureEntity>


)

fun AiWithCategories.toDomain(): Ai {
    return Ai(
        id = this.ai.id,
        name = this.ai.name,
        description = this.ai.description,
        website = this.ai.website,
        priceModel = this.ai.priceModel,
        logoUrl = this.ai.logoUrl,
        categories = this.categories.map { it.toDomain() },
        features = this.features.map { it.toDomain() }
    )
}
