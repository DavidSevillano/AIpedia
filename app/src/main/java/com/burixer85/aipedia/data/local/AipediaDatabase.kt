package com.burixer85.aipedia.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.burixer85.aipedia.data.local.dao.AiDao
import com.burixer85.aipedia.data.local.dao.CategoryDao
import com.burixer85.aipedia.data.local.entity.AiCategoryCrossRef
import com.burixer85.aipedia.data.local.entity.AiFeatureCrossRef
import com.burixer85.aipedia.data.local.entity.AiEntity
import com.burixer85.aipedia.data.local.entity.CategoryEntity
import com.burixer85.aipedia.data.local.entity.FeatureEntity


@Database(
    entities = [AiEntity::class, CategoryEntity::class, AiCategoryCrossRef::class, FeatureEntity::class, AiFeatureCrossRef::class],
    version = 5,
    exportSchema = false
)
abstract class AipediaDatabase : RoomDatabase() {
    abstract fun aiDao(): AiDao
    abstract fun categoryDao(): CategoryDao
}