package com.burixer85.aipedia.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.burixer85.aipedia.data.local.entities.CategoryEntity

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: String): CategoryEntity
}