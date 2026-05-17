package com.burixer85.aipedia.ratings.domain.usecase

import com.burixer85.aipedia.ratings.domain.repository.RatingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SubmitReviewUseCaseTest {

    private val ratingsRepository = mockk<RatingsRepository>(relaxed = true)
    private val submitReview = SubmitReviewUseCase(ratingsRepository)

    @Test
    fun invoke_calls_repository_with_all_params() = runTest {
        submitReview("ai-1", "uid-1", "Juan", 5, "Great tool")

        coVerify(exactly = 1) {
            ratingsRepository.submitReview("ai-1", "uid-1", "Juan", 5, "Great tool")
        }
    }

    @Test
    fun invoke_calls_repository_with_null_body() = runTest {
        submitReview("ai-1", "uid-1", "Juan", 4, null)

        coVerify(exactly = 1) {
            ratingsRepository.submitReview("ai-1", "uid-1", "Juan", 4, null)
        }
    }

    @Test
    fun invoke_propagates_exception_when_repository_throws() = runTest {
        coEvery {
            ratingsRepository.submitReview(any(), any(), any(), any(), any())
        } throws Exception("Unauthorized")

        try {
            submitReview("ai-1", "uid-1", "Juan", 5, "text")
            assert(false) { "Should have thrown" }
        } catch (e: Exception) {
            assertEquals("Unauthorized", e.message)
        }
    }
}
