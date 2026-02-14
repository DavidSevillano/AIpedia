package com.burixer85.aipedia

import android.content.Context
import androidx.room.Room
import com.burixer85.aipedia.data.local.AipediaDatabase
import com.burixer85.aipedia.data.local.dao.AiDao
import com.burixer85.aipedia.data.local.dao.CategoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object CoreModule {

    @Provides
    @Singleton
    fun provideAiDatabase(
        @ApplicationContext context: Context
    ): AipediaDatabase {
        return Room.databaseBuilder(
            context,
            AipediaDatabase::class.java,
            "ai_database",
        ).build()
    }

    @Provides
    @Singleton
    fun provideAiDao(db: AipediaDatabase): AiDao {
        return db.aiDao()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(db: AipediaDatabase): CategoryDao {
        return db.categoryDao()
    }
}




