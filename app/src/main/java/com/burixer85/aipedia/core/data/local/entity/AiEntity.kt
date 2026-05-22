package com.burixer85.aipedia.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai")
data class AiEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val name: String,
    @ColumnInfo(name = "description_es")
    val descriptionEs: String,
    @ColumnInfo(name = "description_en")
    val descriptionEn: String,
    val website: String,
    @ColumnInfo(name = "price_model")
    val priceModel: String,
    @ColumnInfo(name = "logo_url")
    val logoUrl: String? = null,
    @ColumnInfo(name = "is_published")
    val isPublished: Boolean = true,
    val company: String? = null,
    @ColumnInfo(name = "release_year")
    val releaseYear: Int? = null,
    @ColumnInfo(name = "has_api")
    val hasApi: Boolean = false,
    @ColumnInfo(name = "has_free_tier")
    val hasFreeTier: Boolean = false,
    @ColumnInfo(name = "starting_price")
    val startingPrice: Double? = null,
    @ColumnInfo(name = "tagline_es")
    val taglineEs: String? = null,
    @ColumnInfo(name = "tagline_en")
    val taglineEn: String? = null,
)
