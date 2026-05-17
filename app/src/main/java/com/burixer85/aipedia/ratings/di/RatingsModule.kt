package com.burixer85.aipedia.ratings.di

import com.burixer85.aipedia.ratings.data.repository.RatingsRepositoryImp
import com.burixer85.aipedia.ratings.domain.repository.RatingsRepository
import com.burixer85.aipedia.ratings.domain.usecase.GetRatingSummaryUseCase
import com.burixer85.aipedia.ratings.domain.usecase.GetUserReviewsUseCase
import com.burixer85.aipedia.ratings.domain.usecase.GetReviewsUseCase
import com.burixer85.aipedia.ratings.domain.usecase.SubmitRatingUseCase
import com.burixer85.aipedia.ratings.domain.usecase.SubmitReviewUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RatingsModule {

    @Provides
    @Singleton
    fun provideRatingsRepository(supabase: SupabaseClient): RatingsRepository =
        RatingsRepositoryImp(supabase)

    @Provides
    @Singleton
    fun provideGetRatingSummaryUseCase(repo: RatingsRepository) = GetRatingSummaryUseCase(repo)

    @Provides
    @Singleton
    fun provideGetReviewsUseCase(repo: RatingsRepository) = GetReviewsUseCase(repo)

    @Provides
    @Singleton
    fun provideSubmitRatingUseCase(repo: RatingsRepository) = SubmitRatingUseCase(repo)

    @Provides
    @Singleton
    fun provideSubmitReviewUseCase(repo: RatingsRepository) = SubmitReviewUseCase(repo)

    @Provides
    @Singleton
    fun provideGetUserReviewsUseCase(repo: RatingsRepository) = GetUserReviewsUseCase(repo)
}
