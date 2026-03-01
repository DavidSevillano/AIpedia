package com.burixer85.aipedia.home.domain.repository

import com.burixer85.aipedia.core.domain.model.Ai
import com.burixer85.aipedia.core.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getAllAis(): Flow<List<Ai>>
    fun getAllCategories(): Flow<List<Category>>
    suspend fun loadAndCacheInitialData()
}