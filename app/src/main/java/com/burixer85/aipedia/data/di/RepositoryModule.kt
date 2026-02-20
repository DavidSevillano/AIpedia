package com.burixer85.aipedia.data.di

import com.burixer85.aipedia.data.local.HomeRepositoryImp
import com.burixer85.aipedia.domain.HomeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindHomeRepository(homeRepositoryImp: HomeRepositoryImp): HomeRepository
}
