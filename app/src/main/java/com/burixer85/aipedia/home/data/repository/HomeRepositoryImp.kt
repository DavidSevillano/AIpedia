package com.burixer85.aipedia.home.data.repository

import android.util.Log
import com.burixer85.aipedia.core.data.local.dao.AiDao
import com.burixer85.aipedia.core.data.local.dao.CategoryDao
import com.burixer85.aipedia.core.data.local.entity.AiCategoryCrossRef
import com.burixer85.aipedia.core.data.local.entity.toDomain
import com.burixer85.aipedia.core.data.local.entity.AiWithOnlyCategories
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
import com.burixer85.aipedia.core.data.local.entity.AiPlatformCrossRef
import com.burixer85.aipedia.core.domain.model.Category

class HomeRepositoryImp @Inject constructor(
    private val aiDao: AiDao,
    private val categoryDao: CategoryDao,
    private val supabase: SupabaseClient
) : HomeRepository {

    override fun getAllAis(): Flow<List<Ai>> {
        return aiDao.getAllAis()
            .map { listAiWithCategories ->
                listAiWithCategories.map { it.toDomain() }
            }
    }

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories()
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    override suspend fun loadAndCacheInitialData() {
        try {
            val supabaseAis = supabase.from("ai")
                .select(columns = Columns.raw("*, categories(*), capabilities(*), platforms(*)"))
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

            val platformEntities = supabaseAis.flatMap { ai ->
                ai.platforms.map { it.toData() }
            }.distinctBy { it.id }

            val platformRefs = supabaseAis.flatMap { ai ->
                ai.platforms.map { plat ->
                    AiPlatformCrossRef(aiId = ai.id, platformId = plat.id)
                }
            }

            aiDao.insertFeatures(featureEntities)

            aiDao.insertPlatforms(platformEntities)

            categoryDao.insertAll(categoryEntities)

            aiDao.updateData(aiEntities, categoryRefs, featureRefs, platformRefs)

        } catch (e: Exception) {
            Log.e("HomeRepository", "Error en la sincronización: ${e.message}")
        }
    }

}