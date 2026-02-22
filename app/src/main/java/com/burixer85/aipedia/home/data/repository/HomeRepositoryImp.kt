package com.burixer85.aipedia.home.data.repository

import android.util.Log
import com.burixer85.aipedia.core.data.local.dao.AiDao
import com.burixer85.aipedia.core.data.local.dao.CategoryDao
import com.burixer85.aipedia.core.data.local.entity.AiCategoryCrossRef
import com.burixer85.aipedia.core.data.local.entity.toDomain
import com.burixer85.aipedia.home.domain.repository.HomeRepository
import com.burixer85.aipedia.core.domain.model.Ai
import com.burixer85.aipedia.core.domain.model.toData
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import io.github.jan.supabase.postgrest.query.Columns
import com.burixer85.aipedia.core.data.local.entity.AiFeatureCrossRef

class HomeRepositoryImp @Inject constructor(
    private val aiDao: AiDao,
    private val categoryDao: CategoryDao,
    private val supabase: SupabaseClient
) : HomeRepository {

    override fun getAllAisWithCategories(): Flow<List<Ai>> {
        val result = aiDao.getAllAis()
            .map { listAiWithCategories ->
                listAiWithCategories.map { it.toDomain() }
            }
        return result
    }
    override suspend fun loadAndCacheInitialData() {
        try {
            val supabaseAis = supabase.from("ai")
                .select(columns = Columns.raw("*, categories(*), features(*)"))
                .decodeList<Ai>()

            if (supabaseAis.isEmpty()) return

            val aiEntities = supabaseAis.map { it.toData() }

            val categoryEntities = supabaseAis.flatMap { ai ->
                ai.categories.map { it.toData() }
            }.distinctBy { it.id }

            val categoryRefs = supabaseAis.flatMap { ai ->
                ai.categories.map { cat ->
                    AiCategoryCrossRef(aiId = ai.id, categoryId = cat.id)
                }
            }

            val featureEntities = supabaseAis.flatMap { ai ->
                ai.features?.map { it.toData() } ?: emptyList()
            }.distinctBy { it.id }

            val featureRefs = supabaseAis.flatMap { ai ->
                ai.features?.map { feat ->
                    AiFeatureCrossRef(aiId = ai.id, featureId = feat.id)
                } ?: emptyList()
            }

            aiDao.insertFeatures(featureEntities)

            categoryDao.insertAll(categoryEntities)

            aiDao.updateData(aiEntities, categoryRefs, featureRefs)


        } catch (e: Exception) {
            Log.e("HomeRepository", "Error en la sincronización: ${e.message}")
        }
    }

}