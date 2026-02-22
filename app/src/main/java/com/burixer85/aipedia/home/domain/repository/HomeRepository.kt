package com.burixer85.aipedia.home.domain.repository

import com.burixer85.aipedia.core.domain.model.Ai
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getAllAis(): Flow<List<Ai>>
    suspend fun loadAndCacheInitialData()
}