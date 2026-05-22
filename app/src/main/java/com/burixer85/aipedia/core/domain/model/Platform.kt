package com.burixer85.aipedia.core.domain.model

import com.burixer85.aipedia.core.data.local.entity.PlatformEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Platform(
    val id: String,
    val name: String,
    @SerialName("icon_url")
    val icon: String,
    @SerialName("name_es")
    val nameEs: String? = null,
    @SerialName("name_en")
    val nameEn: String? = null,
)

fun Platform.toData(): PlatformEntity {
    return PlatformEntity(
        id = this.id,
        name = this.name,
        iconUrl = this.icon,
        nameEs = this.nameEs,
        nameEn = this.nameEn
    )
}
