package com.burixer85.aipedia.compare.domain.usecase

import com.burixer85.aipedia.core.domain.model.Ai
import com.burixer85.aipedia.home.domain.repository.HomeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetAllAisForPickerUseCaseTest {

    private val repository = mockk<HomeRepository>()
    private val useCase = GetAllAisForPickerUseCase(repository)

    private val testAis = listOf(
        Ai("ai-1", "ChatGPT", "Desc ES", "Desc EN", "https://openai.com", "Freemium", "https://logo.com/chatgpt.png")
    )

    @Test
    fun invoke_returns_flow_from_repository() = runTest {
        every { repository.getAllAis() } returns flowOf(testAis)
        coEvery { repository.loadAndCacheInitialData() } just runs

        val result = useCase().first()

        assertEquals(testAis, result)
    }

    @Test
    fun invoke_triggers_loadAndCacheInitialData_on_start() = runTest {
        every { repository.getAllAis() } returns flowOf(testAis)
        coEvery { repository.loadAndCacheInitialData() } just runs

        useCase().first()

        coVerify(exactly = 1) { repository.loadAndCacheInitialData() }
    }
}
