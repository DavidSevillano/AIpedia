package com.burixer85.aipedia.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.burixer85.aipedia.domain.model.Feature

@Entity(tableName = "features")
data class FeatureEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    @ColumnInfo(name = "icon_url")
    val iconUrl: String
)

fun FeatureEntity.toDomain(): Feature{
    return Feature(
        id = this.id,
        name = this.name,
        description = this.description,
        icon = this.iconUrl
    )
}
