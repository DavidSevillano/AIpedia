package com.burixer85.aipedia.domain

import com.burixer85.aipedia.domain.models.Ai
import kotlinx.coroutines.flow.Flow
import java.util.UUID


interface HomeRepository {
    fun getAllAisWithCategories(): Flow<List<Ai>>
}
