package com.burixer85.aipedia.auth.di

import com.burixer85.aipedia.auth.data.repository.AuthRepositoryImp
import com.burixer85.aipedia.auth.domain.repository.AuthRepository
import com.burixer85.aipedia.auth.domain.usecase.GetCurrentUserUseCase
import com.burixer85.aipedia.auth.domain.usecase.SignOutUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthRepository(supabase: SupabaseClient): AuthRepository =
        AuthRepositoryImp(supabase)

    @Provides
    @Singleton
    fun provideGetCurrentUserUseCase(repo: AuthRepository) = GetCurrentUserUseCase(repo)

    @Provides
    @Singleton
    fun provideSignOutUseCase(repo: AuthRepository) = SignOutUseCase(repo)
}
