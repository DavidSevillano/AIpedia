package com.burixer85.aipedia.domain.models

import com.burixer85.aipedia.data.local.entities.CategoryEntity

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
