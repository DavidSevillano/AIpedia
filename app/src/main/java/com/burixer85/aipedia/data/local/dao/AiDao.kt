package com.burixer85.aipedia.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.burixer85.aipedia.data.local.entities.AiEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AiDao {
    @Query("SELECT * FROM ai")
    suspend fun getAllAis(): Flow<List<AiEntity>>

    @Query("SELECT * FROM ai WHERE id = :aiId")
    suspend fun getAiById(aiId: String): AiEntity?

}