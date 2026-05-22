package com.burixer85.aipedia.core.domain.model

import com.burixer85.aipedia.core.data.local.entity.AiEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Ai(
    val id: String,
    val name: String,
    @SerialName("description_es")
    val descriptionEs: String,
    @SerialName("description_en")
    val descriptionEn: String,
    val website: String,
    @SerialName("price_model")
    val priceModel: String,
    @SerialName("logo_url")
    val logo: String,
    val categories: List<Category> = emptyList(),
    @SerialName("capabilities")
    val features: List<Feature> = emptyList(),
    val platforms: List<Platform> = emptyList(),
    @SerialName("is_published")
    val isPublished: Boolean = true,
    val company: String? = null,
    @SerialName("release_year")
    val releaseYear: Int? = null,
    @SerialName("has_api")
    val hasApi: Boolean = false,
    @SerialName("has_free_tier")
    val hasFreeTier: Boolean = false,
    @SerialName("starting_price")
    val startingPrice: Double? = null,
    @SerialName("tagline_es")
    val taglineEs: String? = null,
    @SerialName("tagline_en")
    val taglineEn: String? = null,
)

fun Ai.toData(): AiEntity {
    return AiEntity(
        id = this.id,
        name = this.name,
        descriptionEs = this.descriptionEs,
        descriptionEn = this.descriptionEn,
        website = this.website,
        priceModel = this.priceModel,
        logoUrl = this.logo,
        isPublished = this.isPublished,
        company = this.company,
        releaseYear = this.releaseYear,
        hasApi = this.hasApi,
        hasFreeTier = this.hasFreeTier,
        startingPrice = this.startingPrice,
        taglineEs = this.taglineEs,
        taglineEn = this.taglineEn,
    )
}
