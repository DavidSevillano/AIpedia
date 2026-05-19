package com.burixer85.aipedia.compare.di

import com.burixer85.aipedia.compare.domain.usecase.GetAllAisForPickerUseCase
import com.burixer85.aipedia.home.domain.repository.HomeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CompareModule {

    @Provides
    @Singleton
    fun provideGetAllAisForPickerUseCase(
        homeRepository: HomeRepository
    ): GetAllAisForPickerUseCase = GetAllAisForPickerUseCase(homeRepository)
}
