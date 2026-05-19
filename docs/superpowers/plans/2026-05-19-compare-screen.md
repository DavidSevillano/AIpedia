# Compare Screen Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a "Comparar" middle tab that lets the user compare 2 AI tools side-by-side across description, price, rating, platforms, categories, and features.

**Architecture:** Stateless `CompareViewModel` (in-memory only) backed by existing `HomeRepository` (Room cache) and `GetRatingSummaryUseCase` (Supabase). AI selection via `ModalBottomSheet` picker. No new storage layer.

**Tech Stack:** Kotlin, Jetpack Compose, Hilt, MockK + coroutines-test, existing `HomeRepository` + `RatingsRepository`.

---

## File Map

| Action | Path |
|--------|------|
| Create | `compare/domain/usecase/GetAllAisForPickerUseCase.kt` |
| Create | `compare/presentation/CompareViewModel.kt` |
| Create | `compare/presentation/AiPickerBottomSheet.kt` |
| Create | `compare/presentation/CompareScreen.kt` |
| Create | `compare/di/CompareModule.kt` |
| Create | `test/.../compare/domain/usecase/GetAllAisForPickerUseCaseTest.kt` |
| Create | `test/.../compare/presentation/CompareViewModelTest.kt` |
| Modify | `navigation/NavigationRoute.kt` |
| Modify | `navigation/NavigationHost.kt` |

All paths are relative to `app/src/main/java/com/burixer85/aipedia/` (and test equivalent).

---

## Task 1: Add `Compare` to `NavigationRoute`

**Files:**
- Modify: `app/src/main/java/com/burixer85/aipedia/navigation/NavigationRoute.kt`

- [ ] **Step 1: Add `Compare` object**

```kotlin
package com.burixer85.aipedia.navigation

sealed class NavigationRoute(val route: String) {
    object Home    : NavigationRoute("home")
    object Ai      : NavigationRoute("ai")
    object Profile : NavigationRoute("profile")
    object Compare : NavigationRoute("compare")
}
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/burixer85/aipedia/navigation/NavigationRoute.kt
git commit -m "feat(compare): add Compare navigation route"
```

---

## Task 2: `GetAllAisForPickerUseCase` + test

**Files:**
- Create: `app/src/main/java/com/burixer85/aipedia/compare/domain/usecase/GetAllAisForPickerUseCase.kt`
- Create: `app/src/test/java/com/burixer85/aipedia/compare/domain/usecase/GetAllAisForPickerUseCaseTest.kt`

- [ ] **Step 1: Write the failing test**

```kotlin
// app/src/test/java/com/burixer85/aipedia/compare/domain/usecase/GetAllAisForPickerUseCaseTest.kt
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
```

- [ ] **Step 2: Run test — expect FAIL (class not found)**

```bash
./gradlew test --tests "com.burixer85.aipedia.compare.domain.usecase.GetAllAisForPickerUseCaseTest"
```

Expected: compilation error — `GetAllAisForPickerUseCase` does not exist yet.

- [ ] **Step 3: Implement use case**

```kotlin
// app/src/main/java/com/burixer85/aipedia/compare/domain/usecase/GetAllAisForPickerUseCase.kt
package com.burixer85.aipedia.compare.domain.usecase

import com.burixer85.aipedia.core.domain.model.Ai
import com.burixer85.aipedia.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GetAllAisForPickerUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    operator fun invoke(): Flow<List<Ai>> =
        homeRepository.getAllAis()
            .onStart {
                try { homeRepository.loadAndCacheInitialData() } catch (_: Exception) {}
            }
}
```

- [ ] **Step 4: Run tests — expect PASS**

```bash
./gradlew test --tests "com.burixer85.aipedia.compare.domain.usecase.GetAllAisForPickerUseCaseTest"
```

Expected: 2 tests PASS.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/burixer85/aipedia/compare/domain/usecase/GetAllAisForPickerUseCase.kt \
        app/src/test/java/com/burixer85/aipedia/compare/domain/usecase/GetAllAisForPickerUseCaseTest.kt
git commit -m "feat(compare): add GetAllAisForPickerUseCase"
```

---

## Task 3: `CompareViewModel` + test

**Files:**
- Create: `app/src/main/java/com/burixer85/aipedia/compare/presentation/CompareViewModel.kt`
- Create: `app/src/test/java/com/burixer85/aipedia/compare/presentation/CompareViewModelTest.kt`

- [ ] **Step 1: Write the failing tests**

```kotlin
// app/src/test/java/com/burixer85/aipedia/compare/presentation/CompareViewModelTest.kt
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
```

- [ ] **Step 2: Run tests — expect FAIL (class not found)**

```bash
./gradlew test --tests "com.burixer85.aipedia.compare.presentation.CompareViewModelTest"
```

Expected: compilation error.

- [ ] **Step 3: Implement `CompareViewModel.kt`**

```kotlin
// app/src/main/java/com/burixer85/aipedia/compare/presentation/CompareViewModel.kt
package com.burixer85.aipedia.compare.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burixer85.aipedia.compare.domain.usecase.GetAllAisForPickerUseCase
import com.burixer85.aipedia.core.domain.model.Ai
import com.burixer85.aipedia.ratings.domain.model.RatingSummary
import com.burixer85.aipedia.ratings.domain.usecase.GetRatingSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class PickerTarget { A, B }

data class CompareUiState(
    val aiA: Ai? = null,
    val aiB: Ai? = null,
    val summaryA: RatingSummary? = null,
    val summaryB: RatingSummary? = null,
    val pickerTarget: PickerTarget? = null,
    val allAis: List<Ai> = emptyList(),
    val pickerQuery: String = ""
) {
    val filteredAis: List<Ai>
        get() = if (pickerQuery.isBlank()) allAis
                else allAis.filter { it.name.contains(pickerQuery, ignoreCase = true) }
}

@HiltViewModel
class CompareViewModel @Inject constructor(
    private val getAllAisForPickerUseCase: GetAllAisForPickerUseCase,
    private val getRatingSummaryUseCase: GetRatingSummaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompareUiState())
    val uiState: StateFlow<CompareUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getAllAisForPickerUseCase()
                .catch { }
                .collect { ais -> _uiState.update { it.copy(allAis = ais) } }
        }
    }

    fun openPicker(target: PickerTarget) =
        _uiState.update { it.copy(pickerTarget = target, pickerQuery = "") }

    fun closePicker() =
        _uiState.update { it.copy(pickerTarget = null, pickerQuery = "") }

    fun onQueryChange(query: String) =
        _uiState.update { it.copy(pickerQuery = query) }

    fun selectAi(ai: Ai) {
        val target = _uiState.value.pickerTarget ?: return
        _uiState.update {
            when (target) {
                PickerTarget.A -> it.copy(aiA = ai, pickerTarget = null, pickerQuery = "")
                PickerTarget.B -> it.copy(aiB = ai, pickerTarget = null, pickerQuery = "")
            }
        }
        fetchSummary(ai.id, target)
    }

    fun clearSlot(target: PickerTarget) =
        _uiState.update {
            when (target) {
                PickerTarget.A -> it.copy(aiA = null, summaryA = null)
                PickerTarget.B -> it.copy(aiB = null, summaryB = null)
            }
        }

    private fun fetchSummary(aiId: String, target: PickerTarget) {
        viewModelScope.launch {
            try {
                val summary = getRatingSummaryUseCase(aiId)
                _uiState.update {
                    when (target) {
                        PickerTarget.A -> it.copy(summaryA = summary)
                        PickerTarget.B -> it.copy(summaryB = summary)
                    }
                }
            } catch (_: Exception) {}
        }
    }
}
```

- [ ] **Step 4: Run tests — expect PASS**

```bash
./gradlew test --tests "com.burixer85.aipedia.compare.presentation.CompareViewModelTest"
```

Expected: 6 tests PASS.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/burixer85/aipedia/compare/presentation/CompareViewModel.kt \
        app/src/test/java/com/burixer85/aipedia/compare/presentation/CompareViewModelTest.kt
git commit -m "feat(compare): add CompareViewModel with state and tests"
```

---

## Task 4: `CompareModule`

**Files:**
- Create: `app/src/main/java/com/burixer85/aipedia/compare/di/CompareModule.kt`

- [ ] **Step 1: Create DI module**

```kotlin
// app/src/main/java/com/burixer85/aipedia/compare/di/CompareModule.kt
package com.burixer85.aipedia.compare.di

import com.burixer85.aipedia.compare.domain.usecase.GetAllAisForPickerUseCase
import com.burixer85.aipedia.home.domain.repository.HomeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CompareModule {

    @Provides
    @Singleton
    fun provideGetAllAisForPickerUseCase(
        homeRepository: HomeRepository
    ): GetAllAisForPickerUseCase = GetAllAisForPickerUseCase(homeRepository)
}
```

- [ ] **Step 2: Verify build compiles**

```bash
./gradlew assembleDebug
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/burixer85/aipedia/compare/di/CompareModule.kt
git commit -m "feat(compare): add CompareModule Hilt DI"
```

---

## Task 5: `AiPickerBottomSheet`

**Files:**
- Create: `app/src/main/java/com/burixer85/aipedia/compare/presentation/AiPickerBottomSheet.kt`

- [ ] **Step 1: Create the composable**

```kotlin
// app/src/main/java/com/burixer85/aipedia/compare/presentation/AiPickerBottomSheet.kt
package com.burixer85.aipedia.compare.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.burixer85.aipedia.core.domain.model.Ai
import com.burixer85.aipedia.core.presentation.component.AIpediaEmptyState
import com.burixer85.aipedia.core.presentation.component.PricePill
import com.burixer85.aipedia.ui.theme.MdOnSurfaceMuted
import com.burixer85.aipedia.ui.theme.MdOnSurfaceStrong
import com.burixer85.aipedia.ui.theme.MdOutline
import com.burixer85.aipedia.ui.theme.MdPrimary
import com.burixer85.aipedia.ui.theme.MdSurfaceContainer
import com.burixer85.aipedia.ui.theme.MdSurfaceHigh

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiPickerBottomSheet(
    ais: List<Ai>,
    query: String,
    onQueryChange: (String) -> Unit,
    onSelect: (Ai) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MdSurfaceContainer
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Elige una IA",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MdOnSurfaceStrong,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Buscar…", color = MdOnSurfaceMuted) },
                leadingIcon = {
                    Icon(Icons.Outlined.Search, contentDescription = null, tint = MdOnSurfaceMuted)
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MdPrimary,
                    unfocusedBorderColor = MdOutline,
                    cursorColor = MdPrimary
                )
            )
            if (ais.isEmpty()) {
                AIpediaEmptyState()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    items(ais, key = { it.id }) { ai ->
                        AiPickerRow(ai = ai, onClick = { onSelect(ai) })
                    }
                }
            }
        }
    }
}

@Composable
private fun AiPickerRow(ai: Ai, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MdSurfaceHigh)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = ai.logo,
            contentDescription = ai.name,
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Text(
            text = ai.name,
            color = MdOnSurfaceStrong,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        PricePill(price = ai.priceModel)
    }
}
```

- [ ] **Step 2: Build to verify no compile errors**

```bash
./gradlew assembleDebug
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/burixer85/aipedia/compare/presentation/AiPickerBottomSheet.kt
git commit -m "feat(compare): add AiPickerBottomSheet composable"
```

---

## Task 6: `CompareScreen`

**Files:**
- Create: `app/src/main/java/com/burixer85/aipedia/compare/presentation/CompareScreen.kt`

- [ ] **Step 1: Create the full screen composable**

```kotlin
// app/src/main/java/com/burixer85/aipedia/compare/presentation/CompareScreen.kt
package com.burixer85.aipedia.compare.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.burixer85.aipedia.core.domain.model.Ai
import com.burixer85.aipedia.core.presentation.component.PricePill
import com.burixer85.aipedia.core.util.localizedDescription
import com.burixer85.aipedia.core.util.localizedName
import com.burixer85.aipedia.ratings.domain.model.RatingSummary
import com.burixer85.aipedia.ui.theme.MdBackground
import com.burixer85.aipedia.ui.theme.MdOnSurfaceMuted
import com.burixer85.aipedia.ui.theme.MdOnSurfaceStrong
import com.burixer85.aipedia.ui.theme.MdOnSurfaceVariant
import com.burixer85.aipedia.ui.theme.MdOutline
import com.burixer85.aipedia.ui.theme.MdOutlineVariant
import com.burixer85.aipedia.ui.theme.MdPrimary
import com.burixer85.aipedia.ui.theme.MdPrimaryContainer
import com.burixer85.aipedia.ui.theme.MdSurfaceContainer

@Composable
fun CompareScreen(
    viewModel: CompareViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MdBackground)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        Text(
            text = "Comparar",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MdOnSurfaceStrong,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)
        )

        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AiSlotCard(
                ai = uiState.aiA,
                onTap = { viewModel.openPicker(PickerTarget.A) },
                onClear = { viewModel.clearSlot(PickerTarget.A) },
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "VS",
                color = MdPrimaryContainer,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp
            )
            AiSlotCard(
                ai = uiState.aiB,
                onTap = { viewModel.openPicker(PickerTarget.B) },
                onClear = { viewModel.clearSlot(PickerTarget.B) },
                modifier = Modifier.weight(1f)
            )
        }

        if (uiState.aiA != null && uiState.aiB != null) {
            Spacer(Modifier.height(16.dp))
            ComparisonTable(
                aiA = uiState.aiA!!,
                aiB = uiState.aiB!!,
                summaryA = uiState.summaryA,
                summaryB = uiState.summaryB,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(Modifier.height(16.dp))
    }

    if (uiState.pickerTarget != null) {
        AiPickerBottomSheet(
            ais = uiState.filteredAis,
            query = uiState.pickerQuery,
            onQueryChange = viewModel::onQueryChange,
            onSelect = viewModel::selectAi,
            onDismiss = viewModel::closePicker
        )
    }
}

// ── Slot card ────────────────────────────────────────────────────────────────

@Composable
private fun AiSlotCard(
    ai: Ai?,
    onTap: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MdSurfaceContainer)
            .border(
                width = if (ai == null) 1.5.dp else 0.dp,
                color = if (ai == null) MdOutline else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onTap)
            .padding(horizontal = 10.dp)
    ) {
        if (ai == null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("+", color = MdPrimary, fontSize = 20.sp, fontWeight = FontWeight.Light)
                Text("Elegir IA", color = MdPrimary, fontSize = 11.sp)
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AsyncImage(
                    model = ai.logo,
                    contentDescription = ai.name,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(7.dp))
                )
                Text(
                    text = ai.name,
                    color = MdOnSurfaceStrong,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onClear,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Quitar",
                        tint = MdOnSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

// ── Comparison table ─────────────────────────────────────────────────────────

@Composable
private fun ComparisonTable(
    aiA: Ai,
    aiB: Ai,
    summaryA: RatingSummary?,
    summaryB: RatingSummary?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MdSurfaceContainer)
    ) {
        CompareRow(label = "Descripción") {
            Text(
                text = aiA.localizedDescription(),
                color = MdOnSurfaceVariant,
                fontSize = 11.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = aiB.localizedDescription(),
                color = MdOnSurfaceVariant,
                fontSize = 11.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
        RowDivider()
        CompareRow(label = "Precio") {
            Box(modifier = Modifier.weight(1f)) { PricePill(price = aiA.priceModel) }
            Box(modifier = Modifier.weight(1f)) { PricePill(price = aiB.priceModel) }
        }
        RowDivider()
        CompareRow(label = "Valoración") {
            RatingCell(summary = summaryA, modifier = Modifier.weight(1f))
            RatingCell(summary = summaryB, modifier = Modifier.weight(1f))
        }
        RowDivider()
        CompareRow(label = "Plataformas") {
            ChipCell(items = aiA.platforms.map { it.name }, modifier = Modifier.weight(1f))
            ChipCell(items = aiB.platforms.map { it.name }, modifier = Modifier.weight(1f))
        }
        RowDivider()
        CompareRow(label = "Categorías") {
            ChipCell(
                items = aiA.categories.map { it.localizedName() },
                modifier = Modifier.weight(1f)
            )
            ChipCell(
                items = aiB.categories.map { it.localizedName() },
                modifier = Modifier.weight(1f)
            )
        }
        RowDivider()
        CompareRow(label = "Características") {
            val allFeatures = (aiA.features + aiB.features).distinctBy { it.id }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                allFeatures.forEach { feature ->
                    FeatureCheckRow(
                        name = feature.localizedName(),
                        has = aiA.features.any { it.id == feature.id }
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                allFeatures.forEach { feature ->
                    FeatureCheckRow(
                        name = feature.localizedName(),
                        has = aiB.features.any { it.id == feature.id }
                    )
                }
            }
        }
    }
}

@Composable
private fun CompareRow(label: String, content: @Composable RowScope.() -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
        Text(
            text = label.uppercase(),
            color = MdOnSurfaceMuted,
            fontSize = 10.sp,
            letterSpacing = 0.8.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(6.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            content = content
        )
    }
}

@Composable
private fun RowDivider() =
    HorizontalDivider(color = MdOutlineVariant, thickness = 1.dp)

@Composable
private fun RatingCell(summary: RatingSummary?, modifier: Modifier = Modifier) {
    if (summary == null) {
        Text("–", color = MdOnSurfaceMuted, fontSize = 12.sp, modifier = modifier)
    } else {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("★", color = Color(0xFFF59E0B), fontSize = 13.sp)
            Text(
                text = "%.1f".format(summary.average),
                color = MdOnSurfaceStrong,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
            Text(
                text = "(${summary.totalCount})",
                color = MdOnSurfaceMuted,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun ChipCell(items: List<String>, modifier: Modifier = Modifier) {
    if (items.isEmpty()) {
        Text("–", color = MdOnSurfaceMuted, fontSize = 11.sp, modifier = modifier)
    } else {
        Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(3.dp)) {
            items.forEach { label ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MdOutlineVariant)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(label, color = MdOnSurfaceVariant, fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
private fun FeatureCheckRow(name: String, has: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = if (has) "✔" else "✘",
            color = if (has) Color(0xFF34D399) else Color(0xFFF87171),
            fontSize = 10.sp
        )
        Text(
            text = name,
            color = if (has) MdOnSurfaceVariant else MdOnSurfaceMuted,
            fontSize = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
```

> **Note:** `Platform` model — check if it has a `.name` field (see `core/domain/model/Platform.kt`). If the field is named differently, update `aiA.platforms.map { it.name }` accordingly.

- [ ] **Step 2: Build to verify**

```bash
./gradlew assembleDebug
```

Expected: BUILD SUCCESSFUL. Fix any compile errors before continuing.

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/burixer85/aipedia/compare/presentation/CompareScreen.kt
git commit -m "feat(compare): add CompareScreen composable"
```

---

## Task 7: Wire into `NavigationHost`

**Files:**
- Modify: `app/src/main/java/com/burixer85/aipedia/navigation/NavigationHost.kt`

- [ ] **Step 1: Add import for `CompareScreen`**

At the top of `NavigationHost.kt`, add:
```kotlin
import com.burixer85.aipedia.compare.presentation.CompareScreen
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.outlined.Compare
```

- [ ] **Step 2: Replace the `"explore"` nav item with Compare**

Find this block in `navItems`:
```kotlin
NavItem("explore", "Explorar", Icons.Filled.Explore, Icons.Outlined.Explore),
```

Replace with:
```kotlin
NavItem(NavigationRoute.Compare.route, "Comparar", Icons.Filled.Compare, Icons.Outlined.Compare),
```

- [ ] **Step 3: Remove the `null` click guard on the middle item**

Find this line in `SharedBottomNav`:
```kotlin
onClick = if (item.route != "explore") { { onItemSelected(item.route) } } else null,
```

Replace with:
```kotlin
onClick = { onItemSelected(item.route) },
```

- [ ] **Step 4: Add the `Compare` composable to `NavHost`**

Inside `NavHost { ... }`, after the `Profile` composable block, add:
```kotlin
composable(NavigationRoute.Compare.route) {
    CompareScreen()
}
```

- [ ] **Step 5: Remove unused `Explore` icon imports**

Remove these two lines from the imports (they're no longer used):
```kotlin
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.outlined.Explore
```

- [ ] **Step 6: Build and verify**

```bash
./gradlew assembleDebug
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step 7: Run all tests to confirm nothing broke**

```bash
./gradlew test
```

Expected: all tests PASS.

- [ ] **Step 8: Commit**

```bash
git add app/src/main/java/com/burixer85/aipedia/navigation/NavigationHost.kt
git commit -m "feat(compare): wire CompareScreen into NavigationHost"
```

---

## Manual Smoke Test

After all tasks pass:

1. Launch app on device/emulator
2. Tap middle "Comparar" tab — screen opens, two empty slots visible
3. Tap left slot — picker sheet opens with search bar + AI list
4. Type "chat" — list filters to matching AIs
5. Tap an AI — slot fills with logo + name
6. Tap right slot — picker opens again
7. Select a different AI — comparison table appears with all 6 rows
8. Rating row shows "–" if no ratings exist, or "★ X.X (N)" if ratings loaded
9. Features row shows ✔/✘ per AI
10. Tap ✕ on a slot — clears it and hides the table
11. Navigate away and back — state resets (stateless by design)
