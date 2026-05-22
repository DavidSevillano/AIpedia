package com.burixer85.aipedia.core.domain.repository

import com.burixer85.aipedia.core.domain.model.ComparisonData

interface ComparisonRepository {
    suspend fun getComparisonData(aiId: String): ComparisonData
}
