package com.burixer85.aipedia.domain

import com.burixer85.aipedia.presentation.models.Ai
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface HomeRepository {
    interface HomeRepository {
        fun getAllAisWithCategories(): Flow<List<Ai>>

        suspend fun getAiDetailsById(aiId: UUID): Ai?
    }
}