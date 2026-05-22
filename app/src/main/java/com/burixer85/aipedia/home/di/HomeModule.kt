package com.burixer85.aipedia.home.di

import com.burixer85.aipedia.home.data.repository.HomeRepositoryImp
import com.burixer85.aipedia.core.data.local.dao.AiDao
import com.burixer85.aipedia.core.data.local.dao.CategoryDao
import com.burixer85.aipedia.home.domain.repository.HomeRepository
import com.burixer85.aipedia.home.domain.usecase.GetAllAisUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeModule {
    @Provides
    @Singleton
    fun provideHomeRepository(
        aiDao: AiDao,
        categoryDao: CategoryDao,
        supabaseClient: SupabaseClient
    ): HomeRepository {
        return HomeRepositoryImp(aiDao, categoryDao, supabaseClient)
    }

    @Provides
    @Singleton
    fun provideGetAllAisUseCase(homeRepository: HomeRepository): GetAllAisUseCase {
        return GetAllAisUseCase(homeRepository)
    }
}
