package com.burixer85.aipedia.core.domain.model

import com.burixer85.aipedia.core.data.local.entity.CategoryEntity
import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: String,
    val name: String,
    val icon: String
)

fun Category.toData(): CategoryEntity {
    return CategoryEntity(
        id = this.id,
        name = this.name,
        icon = this.icon
    )
}

