package com.burixer85.aipedia.core.domain.model

import com.burixer85.aipedia.core.data.local.entity.FeatureEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Feature(
    val id: String,
    @SerialName("name_es")
    val nameEs: String,
    @SerialName("name_en")
    val nameEn: String,
    @SerialName("description_es")
    val descriptionEs: String? = null,
    @SerialName("description_en")
    val descriptionEn: String? = null,
    @SerialName("icon_url")
    val icon: String? = null
)

fun Feature.toData(): FeatureEntity {
    return FeatureEntity(
        id = this.id,
        nameEs = this.nameEs,
        nameEn = this.nameEn,
        descriptionEs = this.descriptionEs,
        descriptionEn = this.descriptionEn,
        iconUrl = this.icon
    )
}

