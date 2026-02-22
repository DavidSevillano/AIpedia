package com.burixer85.aipedia.core.domain.model

import com.burixer85.aipedia.core.data.local.entity.AiEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Ai(
    val id: String,
    val name: String,
    val description: String,
    val website: String,
    @SerialName("price_model")
    val priceModel: String,
    @SerialName("logo_url")
    val logo: String,
    val categories: List<Category> = emptyList(),
    val features: List<Feature> = emptyList()
)

fun Ai.toData(): AiEntity {
    return AiEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        website = this.website,
        priceModel = this.priceModel,
        logoUrl = this.logo,
    )
}
