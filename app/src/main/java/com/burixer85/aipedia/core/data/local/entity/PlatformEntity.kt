package com.burixer85.aipedia.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.burixer85.aipedia.core.domain.model.Feature
import com.burixer85.aipedia.core.domain.model.Platform

@Entity(tableName = "platforms")
data class PlatformEntity(
    @PrimaryKey val id: String,
    val name: String,
    @ColumnInfo(name = "icon_url")
    val iconUrl: String
)

fun PlatformEntity.toDomain(): Platform{
    return Platform(
        id = this.id,
        name = this.name,
        icon = this.iconUrl
    )
}
