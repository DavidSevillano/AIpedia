package com.burixer85.aipedia.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.burixer85.aipedia.data.local.entity.AiCategoryCrossRef
import com.burixer85.aipedia.data.local.entity.AiEntity
import com.burixer85.aipedia.data.local.entity.AiWithCategories
import kotlinx.coroutines.flow.Flow

@Dao
interface AiDao {
    @Transaction
    @Query("SELECT * FROM ai")
    fun getAllAis(): Flow<List<AiWithCategories>>

    @Transaction
    @Query("SELECT * FROM ai WHERE id = :aiId")
    fun getAiById(aiId: String): AiWithCategories?

    @Query("DELETE FROM ai")
    suspend fun deleteAllAis()

    @Query("DELETE FROM aicategorycrossref")
    suspend fun deleteAllAiCategoryCrossRefs()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAis(ais: List<AiEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAiCategoryCrossRefs(crossRefs: List<AiCategoryCrossRef>)

    @Transaction
    suspend fun updateData(ais: List<AiEntity>, crossRefs: List<AiCategoryCrossRef>) {
        deleteAllAiCategoryCrossRefs()
        deleteAllAis()
        insertAis(ais)
        insertAiCategoryCrossRefs(crossRefs)
    }
}



