package com.burixer85.aipedia.core.domain.model

import com.burixer85.aipedia.core.data.local.entity.CategoryEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: String,
    @SerialName("name_es")
    val nameEs: String,
    @SerialName("name_en")
    val nameEn: String,
    @SerialName("icon_url")
    val iconUrl: String? = null,
)

fun Category.toData(): CategoryEntity {
    return CategoryEntity(
        id = this.id,
        nameEs = this.nameEs,
        nameEn = this.nameEn,
        iconUrl = this.iconUrl
    )
}
