package com.burixer85.aipedia.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.burixer85.aipedia.core.domain.model.Platform

@Entity(tableName = "platforms")
data class PlatformEntity(
    @PrimaryKey val id: String,
    val name: String,
    @ColumnInfo(name = "icon_url")
    val iconUrl: String,
    @ColumnInfo(name = "name_es")
    val nameEs: String? = null,
    @ColumnInfo(name = "name_en")
    val nameEn: String? = null,
)

fun PlatformEntity.toDomain(): Platform {
    return Platform(
        id = this.id,
        name = this.name,
        icon = this.iconUrl,
        nameEs = this.nameEs,
        nameEn = this.nameEn
    )
}
