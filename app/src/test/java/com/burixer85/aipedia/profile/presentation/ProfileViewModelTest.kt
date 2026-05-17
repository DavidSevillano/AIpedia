package com.burixer85.aipedia.profile.presentation

import com.burixer85.aipedia.auth.domain.model.AuthUser
import com.burixer85.aipedia.auth.domain.usecase.GetCurrentUserUseCase
import com.burixer85.aipedia.auth.domain.usecase.SignOutUseCase
import com.burixer85.aipedia.ratings.domain.model.Review
import com.burixer85.aipedia.ratings.domain.usecase.GetUserReviewsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val getCurrentUserUseCase = mockk<GetCurrentUserUseCase>()
    private val getUserReviewsUseCase = mockk<GetUserReviewsUseCase>()
    private val signOutUseCase = mockk<SignOutUseCase>(relaxed = true)

    private val mockUser = AuthUser(
        id = "uid-1",
        email = "dvdsevillano@gmail.com",
        displayName = "David Sevillano"
    )

    private val mockReviews = listOf(
        Review("r-1", "ai-1", "uid-1", "David Sevillano", 5, "Excellent", "2026-05-17T10:00:00Z"),
        Review("r-2", "ai-2", "uid-1", "David Sevillano", 3, "Regular", "2026-05-17T11:00:00Z")
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_is_Success_with_correct_stats_when_user_has_reviews() = runTest {
        every { getCurrentUserUseCase() } returns flowOf(mockUser)
        coEvery { getUserReviewsUseCase("uid-1") } returns mockReviews

        val viewModel = ProfileViewModel(getCurrentUserUseCase, getUserReviewsUseCase, signOutUseCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value as ProfileUiState.Success
        assertEquals("David Sevillano", state.displayName)
        assertEquals("dvdsevillano@gmail.com", state.email)
        assertEquals(2, state.reviewCount)
        assertEquals(4.0f, state.averageRating)
        assertEquals(0, state.favoriteCount)
    }

    @Test
    fun averageRating_is_null_when_user_has_no_reviews() = runTest {
        every { getCurrentUserUseCase() } returns flowOf(mockUser)
        coEvery { getUserReviewsUseCase("uid-1") } returns emptyList()

        val viewModel = ProfileViewModel(getCurrentUserUseCase, getUserReviewsUseCase, signOutUseCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value as ProfileUiState.Success
        assertNull(state.averageRating)
        assertEquals(0, state.reviewCount)
    }

    @Test
    fun uiState_stays_Loading_when_user_is_null() = runTest {
        every { getCurrentUserUseCase() } returns flowOf(null)

        val viewModel = ProfileViewModel(getCurrentUserUseCase, getUserReviewsUseCase, signOutUseCase)
        advanceUntilIdle()

        assert(viewModel.uiState.value is ProfileUiState.Loading)
    }

    @Test
    fun signOut_calls_signOutUseCase() = runTest {
        every { getCurrentUserUseCase() } returns flowOf(mockUser)
        coEvery { getUserReviewsUseCase("uid-1") } returns emptyList()

        val viewModel = ProfileViewModel(getCurrentUserUseCase, getUserReviewsUseCase, signOutUseCase)
        advanceUntilIdle()
        viewModel.signOut()
        advanceUntilIdle()

        coVerify(exactly = 1) { signOutUseCase() }
    }
}
