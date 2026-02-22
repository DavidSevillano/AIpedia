package com.burixer85.aipedia.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.burixer85.aipedia.core.data.local.entity.AiCategoryCrossRef
import com.burixer85.aipedia.core.data.local.entity.AiEntity
import com.burixer85.aipedia.core.data.local.entity.AiFeatureCrossRef
import com.burixer85.aipedia.core.data.local.entity.AiWithCategories
import com.burixer85.aipedia.core.data.local.entity.FeatureEntity
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeatures(features: List<FeatureEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAiFeatureCrossRefs(crossRefs: List<AiFeatureCrossRef>)

    @Query("DELETE FROM features")
    suspend fun deleteAllFeatures()

    @Query("DELETE FROM aifeaturecrossref")
    suspend fun deleteAllAiFeatureCrossRefs()

    @Transaction
    suspend fun updateData(
        ais: List<AiEntity>,
        categoryRefs: List<AiCategoryCrossRef>,
        featureRefs: List<AiFeatureCrossRef>
    ) {
        deleteAllAiCategoryCrossRefs()
        deleteAllAiFeatureCrossRefs()
        deleteAllAis()
        insertAis(ais)
        insertAiCategoryCrossRefs(categoryRefs)
        insertAiFeatureCrossRefs(featureRefs)
    }
}



