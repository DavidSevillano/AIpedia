package com.burixer85.aipedia.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.burixer85.aipedia.core.domain.model.Feature

@Entity(tableName = "capabilities")
data class FeatureEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name_es")
    val nameEs: String,
    @ColumnInfo(name = "name_en")
    val nameEn: String,
    @ColumnInfo(name = "description_es")
    val descriptionEs: String? = null,
    @ColumnInfo(name = "description_en")
    val descriptionEn: String? = null,
    @ColumnInfo(name = "icon_url")
    val iconUrl: String? = null
)

fun FeatureEntity.toDomain(): Feature{
    return Feature(
        id = this.id,
        nameEs = this.nameEs,
        nameEn = this.nameEn,
        descriptionEs = this.descriptionEs,
        descriptionEn = this.descriptionEn,
        icon = this.iconUrl
    )
}
