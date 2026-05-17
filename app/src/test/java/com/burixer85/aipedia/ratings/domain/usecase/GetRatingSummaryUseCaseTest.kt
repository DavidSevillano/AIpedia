package com.burixer85.aipedia.ratings.domain.usecase

import com.burixer85.aipedia.ratings.domain.model.RatingSummary
import com.burixer85.aipedia.ratings.domain.repository.RatingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetRatingSummaryUseCaseTest {

    private val ratingsRepository = mockk<RatingsRepository>(relaxed = true)
    private val getRatingSummary = GetRatingSummaryUseCase(ratingsRepository)

    @Test
    fun invoke_returns_summary_from_repository() = runTest {
        val mockSummary = RatingSummary(
            average = 4.3f,
            totalCount = 10,
            distribution = mapOf(5 to 6, 4 to 2, 3 to 1, 2 to 0, 1 to 1)
        )
        coEvery { ratingsRepository.getRatingSummary("ai-1") } returns mockSummary

        val result = getRatingSummary("ai-1")

        assertEquals(mockSummary, result)
        coVerify(exactly = 1) { ratingsRepository.getRatingSummary("ai-1") }
    }

    @Test
    fun invoke_propagates_exception_when_repository_throws() = runTest {
        coEvery { ratingsRepository.getRatingSummary(any()) } throws Exception("Network error")

        try {
            getRatingSummary("ai-1")
            assert(false) { "Should have thrown" }
        } catch (e: Exception) {
            assertEquals("Network error", e.message)
        }
    }
}
