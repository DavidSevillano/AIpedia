package com.burixer85.aipedia.domain.model

import com.burixer85.aipedia.data.local.entity.CategoryEntity
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
fun Category.toPresentation(): com.burixer85.aipedia.presentation.model.Category {
    return com.burixer85.aipedia.presentation.model.Category(
        id = this.id,
        name = this.name,
        icon = this.icon
    )
}
