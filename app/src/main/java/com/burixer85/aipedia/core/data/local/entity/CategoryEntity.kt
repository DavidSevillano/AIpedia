package com.burixer85.aipedia.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.burixer85.aipedia.core.domain.model.Category

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey (autoGenerate = false)
    val id: String,
    val name: String,
    val icon: String
)

fun CategoryEntity.toDomain(): Category {
    return Category(
        id = this.id,
        name = this.name,
        icon = this.icon
    )
}