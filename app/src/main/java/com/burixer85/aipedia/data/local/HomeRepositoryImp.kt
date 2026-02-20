package com.burixer85.aipedia.data.local

import com.burixer85.aipedia.data.local.dao.AiDao
import com.burixer85.aipedia.data.local.entities.toDomain
import com.burixer85.aipedia.domain.HomeRepository
import com.burixer85.aipedia.domain.models.Ai
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HomeRepositoryImp @Inject constructor(private val aiDao: AiDao) : HomeRepository {
    override fun getAllAisWithCategories(): Flow<List<Ai>> {
        val result = aiDao.getAllAis()
            .map { listAiWithCategories ->
                listAiWithCategories.map { it.toDomain() }
            }
        return result
    }
}