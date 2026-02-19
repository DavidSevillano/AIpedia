package com.burixer85.aipedia.data.local.entities

import com.burixer85.aipedia.presentation.models.Ai
import com.burixer85.aipedia.presentation.models.Category

fun AiWithCategories.toPresentation(): Ai {
    return Ai(
        id = this.ai.id,
        name = this.ai.name,
        description = this.ai.description,
        website = this.ai.website,
        price_model = ai.priceModel,
        logo_url = this.ai.logoUrl,
        categories = this.categories.map { it.toPresentation() }
    )
}

fun CategoryEntity.toPresentation(): Category {
    return Category(
        id = this.id,
        name = this.name,
        icon = this.icon
    )
}

fun Ai.toEntity(): AiEntity {
    return AiEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        website = this.website,
        priceModel = this.price_model,
        logoUrl = this.logo_url
    )
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = this.id,
        name = this.name,
        icon = this.icon
    )
}



