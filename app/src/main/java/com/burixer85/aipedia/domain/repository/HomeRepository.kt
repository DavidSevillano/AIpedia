package com.burixer85.aipedia.domain.repository

import com.burixer85.aipedia.domain.model.Ai
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getAllAisWithCategories(): Flow<List<Ai>>
    suspend fun loadAndCacheInitialData()
}