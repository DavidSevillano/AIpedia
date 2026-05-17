package com.burixer85.aipedia.ratings.domain.usecase

import com.burixer85.aipedia.ratings.domain.model.Review
import com.burixer85.aipedia.ratings.domain.repository.RatingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetReviewsUseCaseTest {

    private val ratingsRepository = mockk<RatingsRepository>(relaxed = true)
    private val getReviews = GetReviewsUseCase(ratingsRepository)

    private val mockReview = Review(
        id = "r-1", aiId = "ai-1", userId = "uid-1",
        displayName = "Juan", score = 5,
        body = "Excellent tool", createdAt = "2026-05-17T10:00:00Z"
    )

    @Test
    fun invoke_returns_reviews_from_repository() = runTest {
        coEvery { ratingsRepository.getReviews("ai-1") } returns listOf(mockReview)

        val result = getReviews("ai-1")

        assertEquals(1, result.size)
        assertEquals(mockReview, result[0])
        coVerify(exactly = 1) { ratingsRepository.getReviews("ai-1") }
    }

    @Test
    fun invoke_returns_empty_list_when_no_reviews() = runTest {
        coEvery { ratingsRepository.getReviews("ai-1") } returns emptyList()

        val result = getReviews("ai-1")

        assertEquals(0, result.size)
    }
}
