package com.burixer85.aipedia.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.burixer85.aipedia.core.domain.model.Category

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    @ColumnInfo("name_es")
    val nameEs: String,
    @ColumnInfo("name_en")
    val nameEn: String,
    @ColumnInfo("icon_url")
    val iconUrl: String? = null,
)

fun CategoryEntity.toDomain(): Category {
    return Category(
        id = this.id,
        nameEs = this.nameEs,
        nameEn = this.nameEn,
        iconUrl = this.iconUrl
    )
}
