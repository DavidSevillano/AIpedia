package com.burixer85.aipedia.domain.models

import com.burixer85.aipedia.data.local.entities.AiEntity

data class Ai(
    val id: String,
    val name: String,
    val description: String,
    val website: String,
    val priceModel: String,
    val logoUrl: String,
    val categories: List<Category>
)
fun Ai.toData(): AiEntity{
    return AiEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        website = this.website,
        priceModel = this.priceModel,
        logoUrl = this.logoUrl,
    )
}