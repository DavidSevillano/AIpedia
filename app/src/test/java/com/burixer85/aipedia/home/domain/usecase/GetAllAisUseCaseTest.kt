package com.burixer85.aipedia.home.domain.usecase

import com.burixer85.aipedia.core.domain.model.Ai
import com.burixer85.aipedia.home.domain.repository.HomeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test


class GetAllAisUseCaseTest {

    private val homeRepository = mockk<HomeRepository>(relaxed = true)

    private val getAllAis = GetAllAisUseCase(homeRepository)

    @Test
    fun invoke_emits_data_from_repository_and_trigger_sync() = runTest {
        val mockAiList = listOf(
            Ai(
                id = "1", name = "Test AI", descriptionEs = "", descriptionEn = "",
                website = "", priceModel = "", logo = ""
            )
        )

        coEvery { homeRepository.getAllAis() } returns flowOf(mockAiList)

        val result = getAllAis().first()

        assertEquals(mockAiList, result)

        coVerify(exactly = 1) { homeRepository.loadAndCacheInitialData() }
    }

    @Test
    fun invoke_emits_error_when_repository_fails() = runTest {
        coEvery { homeRepository.getAllAis() } throws Exception("Network error")

        try {
            getAllAis().first()
        } catch (e: Exception) {
            assertEquals("Network error", e.message)
        }
    }

    @Test
    fun invoke_emits_data_even_if_sync_fails() = runTest {
        val mockAiList = listOf(
            Ai(
                id = "1", name = "Test AI", descriptionEs = "", descriptionEn = "",
                website = "", priceModel = "", logo = ""
            )
        )
        coEvery { homeRepository.getAllAis() } returns flowOf(mockAiList)
        coEvery { homeRepository.loadAndCacheInitialData() } throws Exception("Sync failed")

        val result = getAllAis().first()

        assertEquals(mockAiList, result)
    }

    @Test
    fun invoke_emits_empty_list_when_repository_returns_empty_flow() = runTest {
        coEvery { homeRepository.getAllAis() } returns emptyFlow()

        val results = mutableListOf<List<Ai>>()
        getAllAis().toList(results)

        assertEquals(0, results.size)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun invoke_emits_new_data_when_repository_emits_updates() = runTest {
        val sharedFlow = MutableSharedFlow<List<Ai>>()
        coEvery { homeRepository.getAllAis() } returns sharedFlow

        val results = mutableListOf<List<Ai>>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            getAllAis().toList(results)
        }

        val listA = listOf(Ai(id = "1", name = "AI A", descriptionEs = "", descriptionEn = "", website = "", priceModel = "", logo = ""))
        val listB = listOf(
            Ai(id = "1", name = "AI A", descriptionEs = "", descriptionEn = "", website = "", priceModel = "", logo = ""),
            Ai(id = "2", name = "AI B", descriptionEs = "", descriptionEn = "", website = "", priceModel = "", logo = "")
        )

        sharedFlow.emit(listA)
        sharedFlow.emit(listB)

        assertEquals(2, results.size)
        assertEquals("AI A", results[0][0].name)
        assertEquals("AI B", results[1][1].name)

        job.cancel()
    }

}