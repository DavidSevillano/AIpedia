package com.burixer85.aipedia.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai")
data class AiEntity(
    @PrimaryKey (autoGenerate = false)
    val id: String,
    val name: String,
    val description: String,
    val website: String,
    @ColumnInfo(name = "price_model")
    val priceModel: Double,
    @ColumnInfo(name = "logo_url")
    val logoUrl: String
)