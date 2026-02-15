package com.burixer85.aipedia.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.burixer85.aipedia.data.local.entities.AiWithCategories
import kotlinx.coroutines.flow.Flow

@Dao
interface AiDao {
    @Transaction
    @Query("SELECT * FROM ai")
    fun getAllAis(): Flow<List<AiWithCategories>>

    @Transaction
    @Query("SELECT * FROM ai WHERE id = :aiId")
    fun getAiById(aiId: String): AiWithCategories?

}