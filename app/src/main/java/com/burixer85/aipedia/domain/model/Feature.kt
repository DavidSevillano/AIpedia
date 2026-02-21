package com.burixer85.aipedia.domain.model

import com.burixer85.aipedia.data.local.entity.FeatureEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Feature(
    val id: String,
    val name: String,
    val description: String,
    @SerialName("icon_url")
    val icon: String
)

fun Feature.toData(): FeatureEntity {
    return FeatureEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        iconUrl = this.icon
    )
}

