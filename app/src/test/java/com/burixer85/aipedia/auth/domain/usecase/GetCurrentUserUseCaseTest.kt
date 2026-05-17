package com.burixer85.aipedia.auth.domain.usecase

import com.burixer85.aipedia.auth.domain.model.AuthUser
import com.burixer85.aipedia.auth.domain.repository.AuthRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GetCurrentUserUseCaseTest {

    private val authRepository = mockk<AuthRepository>(relaxed = true)
    private val getCurrentUser = GetCurrentUserUseCase(authRepository)

    @Test
    fun invoke_emits_user_when_session_exists() = runTest {
        val mockUser = AuthUser(id = "uid-1", email = "test@test.com", displayName = "Test User")
        every { authRepository.getCurrentUser() } returns flowOf(mockUser)

        val result = getCurrentUser().first()

        assertEquals(mockUser, result)
    }

    @Test
    fun invoke_emits_null_when_no_session() = runTest {
        every { authRepository.getCurrentUser() } returns flowOf(null)

        val result = getCurrentUser().first()

        assertNull(result)
    }
}
