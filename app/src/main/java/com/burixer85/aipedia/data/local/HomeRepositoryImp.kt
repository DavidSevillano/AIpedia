package com.burixer85.aipedia.data.local

import android.util.Log
import androidx.compose.foundation.layout.size
import com.burixer85.aipedia.data.local.dao.AiDao
import com.burixer85.aipedia.data.local.dao.CategoryDao
import com.burixer85.aipedia.data.local.entity.AiCategoryCrossRef
import com.burixer85.aipedia.data.local.entity.AiEntity
import com.burixer85.aipedia.data.local.entity.CategoryEntity
import com.burixer85.aipedia.data.local.entity.toDomain
import com.burixer85.aipedia.domain.repository.HomeRepository
import com.burixer85.aipedia.domain.model.Ai
import com.burixer85.aipedia.domain.model.toData
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import javax.inject.Inject
import io.github.jan.supabase.postgrest.query.Columns

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
                .select(columns = Columns.raw("*, categories(*)"))
                .decodeList<Ai>()

            if (supabaseAis.isEmpty()) return

            val aiEntities = supabaseAis.map { it.toData() }

            val categoryEntities = supabaseAis.flatMap { ai ->
                ai.categories.map { it.toData() }
            }.distinctBy { it.id }

            val crossRefs = supabaseAis.flatMap { ai ->
                ai.categories.map { cat ->
                    AiCategoryCrossRef(aiId = ai.id, categoryId = cat.id)
                }
            }

            categoryDao.insertAll(categoryEntities)

            aiDao.updateData(aiEntities, crossRefs)

        } catch (e: Exception) {
            Log.e("HomeRepository", "Error en la sincronización: ${e.message}")
        }
    }

}