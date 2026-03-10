package com.burixer85.aipedia.home.domain.usecase

import com.burixer85.aipedia.core.domain.model.Category
import com.burixer85.aipedia.home.domain.repository.HomeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test


class GetAllCategoriesUseCaseTest {

    private val repository: HomeRepository = mockk(relaxed = true)
    private val getAllCategoriesUseCase = GetAllCategoriesUseCase(repository)

    @Test
    fun invoke_emits_data_from_repository() = runTest {
        val mockCategories = listOf(Category(id = "1", nameEs = "AI", nameEn = "AI"))

        coEvery { repository.getAllCategories() } returns flowOf(mockCategories)

        val result = getAllCategoriesUseCase().first()

        assertEquals(mockCategories, result)
        coVerify(exactly = 1) { repository.getAllCategories() }
    }

    @Test
    fun invoke_emits_error_when_repository_fails() = runTest {
        coEvery { repository.getAllCategories() } throws Exception("Database error")

        try {
            getAllCategoriesUseCase().first()
            fail("Should have thrown an exception")
        } catch (e: Exception) {
            assertEquals("Database error", e.message)
        }
    }

    @Test
    fun invoke_emits_empty_list_when_repository_returns_empty_flow() = runTest {
        coEvery { repository.getAllCategories() } returns emptyFlow()

        val results = mutableListOf<List<Category>>()

        getAllCategoriesUseCase().toList(results)

        assertEquals(0, results.size)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun invoke_emits_new_data_when_repository_emits_updates() = runTest {
        val sharedFlow = MutableSharedFlow<List<Category>>()

        coEvery { repository.getAllCategories() } returns sharedFlow

        val results = mutableListOf<List<Category>>()

        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            getAllCategoriesUseCase().toList(results)
        }

        val listA = listOf(Category(id = "1", nameEs = "A", nameEn = "A"))
        val listB = listOf(
            Category(id = "1", nameEs = "A", nameEn = "A"),
            Category(id = "2", nameEs = "B", nameEn = "B")
        )

        sharedFlow.emit(listA)
        sharedFlow.emit(listB)

        assertEquals(2, results.size)
        assertEquals("A", results[0][0].nameEs)
        assertEquals("B", results[1][1].nameEs)

        job.cancel()
    }
}