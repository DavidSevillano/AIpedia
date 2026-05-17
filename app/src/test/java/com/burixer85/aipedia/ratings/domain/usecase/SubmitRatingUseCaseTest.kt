package com.burixer85.aipedia.ratings.domain.usecase

import com.burixer85.aipedia.ratings.domain.repository.RatingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SubmitRatingUseCaseTest {

    private val ratingsRepository = mockk<RatingsRepository>(relaxed = true)
    private val submitRating = SubmitRatingUseCase(ratingsRepository)

    @Test
    fun invoke_calls_repository_with_correct_params() = runTest {
        submitRating("ai-1", "device-123", 4)

        coVerify(exactly = 1) { ratingsRepository.submitRating("ai-1", "device-123", 4) }
    }

    @Test
    fun invoke_propagates_exception_when_repository_throws() = runTest {
        coEvery { ratingsRepository.submitRating(any(), any(), any()) } throws Exception("Network error")

        try {
            submitRating("ai-1", "device-123", 4)
            assert(false) { "Should have thrown" }
        } catch (e: Exception) {
            assertEquals("Network error", e.message)
        }
    }
}
