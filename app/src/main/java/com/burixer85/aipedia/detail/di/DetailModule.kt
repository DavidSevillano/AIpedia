package com.burixer85.aipedia.detail.di

import com.burixer85.aipedia.core.data.local.dao.AiDao
import com.burixer85.aipedia.core.data.local.dao.CategoryDao
import com.burixer85.aipedia.detail.data.repository.DetailRepositoryImp
import com.burixer85.aipedia.detail.domain.repository.DetailRepository
import com.burixer85.aipedia.detail.domain.usecase.GetAiUseCase
import com.burixer85.aipedia.home.domain.usecase.GetAllAisUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DetailModule {
    @Provides
    @Singleton
    fun provideDetailRepository(aiDao: AiDao): DetailRepository {
        return DetailRepositoryImp(aiDao)
    }

    @Provides
    @Singleton
    fun provideGetAiUseCase(detailRepository: DetailRepository): GetAiUseCase {
        return GetAiUseCase(detailRepository)
    }
}
