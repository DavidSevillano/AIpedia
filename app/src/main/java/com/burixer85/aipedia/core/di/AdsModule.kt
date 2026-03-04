package com.burixer85.aipedia.core.di

import com.burixer85.aipedia.core.data.ads.repository.AdRepositoryImpl
import com.burixer85.aipedia.core.domain.repository.AdRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AdsModule {

    @Binds
    @Singleton
    abstract fun bindAdRepository(
        adRepositoryImpl: AdRepositoryImpl
    ): AdRepository
}