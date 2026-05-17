package com.burixer85.aipedia.ratings.domain.usecase

import com.burixer85.aipedia.ratings.domain.model.Review
import com.burixer85.aipedia.ratings.domain.repository.RatingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetUserReviewsUseCaseTest {

    private val ratingsRepository = mockk<RatingsRepository>(relaxed = true)
    private val getUserReviews = GetUserReviewsUseCase(ratingsRepository)

    private val mockReview = Review(
        id = "r-1", aiId = "ai-1", userId = "uid-1",
        displayName = "David", score = 4,
        body = "Muy buena herramienta", createdAt = "2026-05-17T10:00:00Z"
    )

    @Test
    fun invoke_returns_user_reviews_from_repository() = runTest {
        coEvery { ratingsRepository.getUserReviews("uid-1") } returns listOf(mockReview)

        val result = getUserReviews("uid-1")

        assertEquals(1, result.size)
        assertEquals(mockReview, result[0])
        coVerify(exactly = 1) { ratingsRepository.getUserReviews("uid-1") }
    }

    @Test
    fun invoke_returns_empty_list_when_user_has_no_reviews() = runTest {
        coEvery { ratingsRepository.getUserReviews("uid-1") } returns emptyList()

        val result = getUserReviews("uid-1")

        assertEquals(0, result.size)
    }
}
