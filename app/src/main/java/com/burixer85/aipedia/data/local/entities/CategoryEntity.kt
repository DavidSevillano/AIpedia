package com.burixer85.aipedia.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey (autoGenerate = false)
    val id: String,
    val name: String,
    val icon: String
)