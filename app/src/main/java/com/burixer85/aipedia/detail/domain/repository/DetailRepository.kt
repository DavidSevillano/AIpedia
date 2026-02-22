package com.burixer85.aipedia.detail.domain.repository

import com.burixer85.aipedia.core.domain.model.Ai
import java.util.UUID

interface DetailRepository {
    suspend fun getAiById(aiId: UUID): Ai?
}