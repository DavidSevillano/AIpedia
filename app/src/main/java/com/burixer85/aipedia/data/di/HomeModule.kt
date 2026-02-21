package com.burixer85.aipedia.data.di

import com.burixer85.aipedia.data.local.HomeRepositoryImp
import com.burixer85.aipedia.data.local.dao.AiDao
import com.burixer85.aipedia.data.local.dao.CategoryDao
import com.burixer85.aipedia.domain.repository.HomeRepository
import com.burixer85.aipedia.domain.usecase.HomeUseCase
import dagger.Binds
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
    fun provideHomeRepository(aiDao: AiDao, categoryDao: CategoryDao, supabaseClient: SupabaseClient): HomeRepository {
        return HomeRepositoryImp(aiDao, categoryDao, supabaseClient)
    }

    @Provides
    @Singleton
    fun provideHomeUseCase(homeRepository: HomeRepository): HomeUseCase {
        return HomeUseCase(homeRepository)
    }

}
