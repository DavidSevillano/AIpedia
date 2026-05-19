package com.burixer85.aipedia.compare.presentation

import com.burixer85.aipedia.compare.domain.usecase.GetAllAisForPickerUseCase
import com.burixer85.aipedia.core.domain.model.Ai
import com.burixer85.aipedia.ratings.domain.model.RatingSummary
import com.burixer85.aipedia.ratings.domain.usecase.GetRatingSummaryUseCase
import io.mockk.coEvery
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
class CompareViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getAllAisForPickerUseCase = mockk<GetAllAisForPickerUseCase>()
    private val getRatingSummaryUseCase = mockk<GetRatingSummaryUseCase>()

    private val aiA = Ai("ai-1", "ChatGPT", "Desc ES", "Desc EN", "https://openai.com", "Freemium", "https://logo.com/chatgpt.png")
    private val aiB = Ai("ai-2", "Claude", "Desc ES", "Desc EN", "https://claude.ai", "Freemium", "https://logo.com/claude.png")
    private val mockSummary = RatingSummary(4.5f, 100, mapOf(5 to 70, 4 to 20, 3 to 5, 2 to 3, 1 to 2))

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { getAllAisForPickerUseCase() } returns flowOf(listOf(aiA, aiB))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = CompareViewModel(getAllAisForPickerUseCase, getRatingSummaryUseCase)

    @Test
    fun init_loads_all_ais_into_allAis() = runTest {
        val viewModel = buildViewModel()
        advanceUntilIdle()
        assertEquals(listOf(aiA, aiB), viewModel.uiState.value.allAis)
    }

    @Test
    fun openPicker_sets_pickerTarget_and_clears_query() = runTest {
        val viewModel = buildViewModel()
        viewModel.openPicker(PickerTarget.A)
        assertEquals(PickerTarget.A, viewModel.uiState.value.pickerTarget)
        assertEquals("", viewModel.uiState.value.pickerQuery)
    }

    @Test
    fun onQueryChange_filters_filteredAis_by_name() = runTest {
        val viewModel = buildViewModel()
        advanceUntilIdle()
        viewModel.openPicker(PickerTarget.A)
        viewModel.onQueryChange("Chat")
        assertEquals(listOf(aiA), viewModel.uiState.value.filteredAis)
    }

    @Test
    fun selectAi_sets_aiA_clears_picker_and_fetches_summary() = runTest {
        coEvery { getRatingSummaryUseCase("ai-1") } returns mockSummary
        val viewModel = buildViewModel()
        advanceUntilIdle()
        viewModel.openPicker(PickerTarget.A)
        viewModel.selectAi(aiA)
        advanceUntilIdle()
        assertEquals(aiA, viewModel.uiState.value.aiA)
        assertNull(viewModel.uiState.value.pickerTarget)
        assertEquals(mockSummary, viewModel.uiState.value.summaryA)
    }

    @Test
    fun clearSlot_A_nulls_aiA_and_summaryA() = runTest {
        coEvery { getRatingSummaryUseCase("ai-1") } returns mockSummary
        val viewModel = buildViewModel()
        advanceUntilIdle()
        viewModel.openPicker(PickerTarget.A)
        viewModel.selectAi(aiA)
        advanceUntilIdle()
        viewModel.clearSlot(PickerTarget.A)
        assertNull(viewModel.uiState.value.aiA)
        assertNull(viewModel.uiState.value.summaryA)
    }

    @Test
    fun fetchSummary_failure_is_silent_and_summary_stays_null() = runTest {
        coEvery { getRatingSummaryUseCase("ai-1") } throws RuntimeException("network error")
        val viewModel = buildViewModel()
        advanceUntilIdle()
        viewModel.openPicker(PickerTarget.A)
        viewModel.selectAi(aiA)
        advanceUntilIdle()
        assertEquals(aiA, viewModel.uiState.value.aiA)
        assertNull(viewModel.uiState.value.summaryA)
    }
}
