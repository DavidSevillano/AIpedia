package com.burixer85.aipedia.core.data.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.burixer85.aipedia.core.domain.model.Ai


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
    val features: List<FeatureEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(AiPlatformCrossRef::class, parentColumn = "aiId", entityColumn = "platformId")
    )
    val platforms: List<PlatformEntity>
)

fun AiWithCategories.toDomain(): Ai {
    return Ai(
        id = this.ai.id,
        name = this.ai.name,
        descriptionEs = this.ai.descriptionEs,
        descriptionEn = this.ai.descriptionEn,
        website = this.ai.website,
        priceModel = this.ai.priceModel,
        logo = this.ai.logoUrl,
        categories = this.categories.map { it.toDomain() },
        features = this.features.map { it.toDomain() },
        platforms = this.platforms.map { it.toDomain() },
        isPublished = this.ai.isPublished
    )
}
