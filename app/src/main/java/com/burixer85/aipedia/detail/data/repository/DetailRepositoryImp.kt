package com.burixer85.aipedia.detail.data.repository

import com.burixer85.aipedia.core.data.local.dao.AiDao
import com.burixer85.aipedia.core.data.local.entity.toDomain
import com.burixer85.aipedia.core.domain.model.Ai
import javax.inject.Inject
import com.burixer85.aipedia.detail.domain.repository.DetailRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class DetailRepositoryImp @Inject constructor(
    private val aiDao: AiDao,
) : DetailRepository {

    override suspend fun getAiById(aiId: UUID): Ai? {
        return withContext(Dispatchers.IO) {

            val result = aiDao.getAiById(aiId.toString())

            result?.toDomain()
        }
    }
}