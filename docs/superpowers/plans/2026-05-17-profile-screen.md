# Profile Screen Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Reemplazar el 3er item del bottom nav ("Guardados") por "Perfil" y crear la pantalla de perfil con avatar, estadisticas del usuario y boton de cierre de sesion.

**Architecture:** El bottom nav se saca de `HomeScreen` y pasa a `NavigationHost` como componente compartido. Se anade `NavigationRoute.Profile` y un nuevo feature module `profile/` siguiendo el patron MVVM + Clean Architecture existente. Las estadisticas (resenas y rating medio) se obtienen con un nuevo `GetUserReviewsUseCase` que filtra `ai_reviews` por `user_id`.

**Tech Stack:** Kotlin, Jetpack Compose, Hilt, StateFlow, Supabase PostgREST, MockK + JUnit 4 (tests)

---

## File Map

| Accion | Ruta |
|--------|------|
| **Crear** | `ratings/domain/usecase/GetUserReviewsUseCase.kt` |
| **Modificar** | `ratings/domain/repository/RatingsRepository.kt` |
| **Modificar** | `ratings/data/repository/RatingsRepositoryImp.kt` |
| **Modificar** | `ratings/di/RatingsModule.kt` |
| **Crear** | `profile/presentation/ProfileUiState.kt` |
| **Crear** | `profile/presentation/ProfileViewModel.kt` |
| **Crear** | `profile/presentation/ProfileScreen.kt` |
| **Crear** | `profile/di/ProfileModule.kt` |
| **Modificar** | `navigation/NavigationRoute.kt` |
| **Modificar** | `navigation/NavigationHost.kt` |
| **Modificar** | `home/presentation/HomeScreen.kt` |
| **Crear** | `test/.../ratings/domain/usecase/GetUserReviewsUseCaseTest.kt` |
| **Crear** | `test/.../profile/presentation/ProfileViewModelTest.kt` |

Todos los paths son relativos a `app/src/main/java/com/burixer85/aipedia/` (o `app/src/test/java/com/burixer85/aipedia/` para tests).

---

## Task 1: GetUserReviewsUseCase + repo

**Files:**
- Modify: `app/src/main/java/com/burixer85/aipedia/ratings/domain/repository/RatingsRepository.kt`
- Modify: `app/src/main/java/com/burixer85/aipedia/ratings/data/repository/RatingsRepositoryImp.kt`
- Create: `app/src/main/java/com/burixer85/aipedia/ratings/domain/usecase/GetUserReviewsUseCase.kt`
- Modify: `app/src/main/java/com/burixer85/aipedia/ratings/di/RatingsModule.kt`
- Create: `app/src/test/java/com/burixer85/aipedia/ratings/domain/usecase/GetUserReviewsUseCaseTest.kt`

- [ ] **Paso 1: Escribir el test que falla**

Crea `app/src/test/java/com/burixer85/aipedia/ratings/domain/usecase/GetUserReviewsUseCaseTest.kt`:

```kotlin
package com.burixer85.aipedia.ratings.domain.usecase

import com.burixer85.aipedia.ratings.domain.model.Review
import com.burixer85.aipedia.ratings.domain.repository.RatingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetUserReviewsUseCaseTest {

    private val ratingsRepository = mockk<RatingsRepository>(relaxed = true)
    private val getUserReviews = GetUserReviewsUseCase(ratingsRepository)

    private val mockReview = Review(
        id = "r-1", aiId = "ai-1", userId = "uid-1",
        displayName = "David", score = 4,
        body = "Muy buena herramienta", createdAt = "2026-05-17T10:00:00Z"
    )

    @Test
    fun invoke_returns_user_reviews_from_repository() = runTest {
        coEvery { ratingsRepository.getUserReviews("uid-1") } returns listOf(mockReview)

        val result = getUserReviews("uid-1")

        assertEquals(1, result.size)
        assertEquals(mockReview, result[0])
        coVerify(exactly = 1) { ratingsRepository.getUserReviews("uid-1") }
    }

    @Test
    fun invoke_returns_empty_list_when_user_has_no_reviews() = runTest {
        coEvery { ratingsRepository.getUserReviews("uid-1") } returns emptyList()

        val result = getUserReviews("uid-1")

        assertEquals(0, result.size)
    }
}
```

- [ ] **Paso 2: Ejecutar el test para verificar que falla**

```
./gradlew test --tests "com.burixer85.aipedia.ratings.domain.usecase.GetUserReviewsUseCaseTest"
```

Esperado: FAIL — `GetUserReviewsUseCase` y `getUserReviews` no existen aun.

- [ ] **Paso 3: Anadir metodo a la interfaz del repositorio**

Reemplaza el contenido completo de `app/src/main/java/com/burixer85/aipedia/ratings/domain/repository/RatingsRepository.kt`:

```kotlin
package com.burixer85.aipedia.ratings.domain.repository

import com.burixer85.aipedia.ratings.domain.model.RatingSummary
import com.burixer85.aipedia.ratings.domain.model.Review

interface RatingsRepository {
    suspend fun getRatingSummary(aiId: String): RatingSummary
    suspend fun getReviews(aiId: String): List<Review>
    suspend fun getUserRating(aiId: String, deviceId: String): Int?
    suspend fun getUserReview(aiId: String, userId: String): Review?
    suspend fun submitRating(aiId: String, deviceId: String, score: Int)
    suspend fun submitReview(aiId: String, userId: String, displayName: String, score: Int, body: String?)
    suspend fun getUserReviews(userId: String): List<Review>
}
```

- [ ] **Paso 4: Implementar en RatingsRepositoryImp**

En `app/src/main/java/com/burixer85/aipedia/ratings/data/repository/RatingsRepositoryImp.kt`, anade este metodo al final de la clase, justo antes del cierre `}`:

```kotlin
    override suspend fun getUserReviews(userId: String): List<Review> =
        supabase.from("ai_reviews")
            .select { filter { eq("user_id", userId) } }
            .decodeList<ReviewDto>()
            .map { it.toDomain() }
```

- [ ] **Paso 5: Crear GetUserReviewsUseCase**

Crea `app/src/main/java/com/burixer85/aipedia/ratings/domain/usecase/GetUserReviewsUseCase.kt`:

```kotlin
package com.burixer85.aipedia.ratings.domain.usecase

import com.burixer85.aipedia.ratings.domain.model.Review
import com.burixer85.aipedia.ratings.domain.repository.RatingsRepository
import javax.inject.Inject

class GetUserReviewsUseCase @Inject constructor(
    private val ratingsRepository: RatingsRepository
) {
    suspend operator fun invoke(userId: String): List<Review> =
        ratingsRepository.getUserReviews(userId)
}
```

- [ ] **Paso 6: Exponer en RatingsModule**

En `app/src/main/java/com/burixer85/aipedia/ratings/di/RatingsModule.kt`, anade este import al bloque de imports del archivo:

```kotlin
import com.burixer85.aipedia.ratings.domain.usecase.GetUserReviewsUseCase
```

Y anade este metodo al final del objeto `RatingsModule`, antes del cierre `}`:

```kotlin
    @Provides
    @Singleton
    fun provideGetUserReviewsUseCase(repo: RatingsRepository) = GetUserReviewsUseCase(repo)
```

- [ ] **Paso 7: Ejecutar el test para verificar que pasa**

```
./gradlew test --tests "com.burixer85.aipedia.ratings.domain.usecase.GetUserReviewsUseCaseTest"
```

Esperado: PASS (2 tests)

- [ ] **Paso 8: Commit**

```
git add app/src/main/java/com/burixer85/aipedia/ratings/domain/repository/RatingsRepository.kt
git add app/src/main/java/com/burixer85/aipedia/ratings/data/repository/RatingsRepositoryImp.kt
git add app/src/main/java/com/burixer85/aipedia/ratings/domain/usecase/GetUserReviewsUseCase.kt
git add app/src/main/java/com/burixer85/aipedia/ratings/di/RatingsModule.kt
git add app/src/test/java/com/burixer85/aipedia/ratings/domain/usecase/GetUserReviewsUseCaseTest.kt
git commit -m "feat: add getUserReviews to RatingsRepository and use case"
```

---

## Task 2: ProfileUiState + ProfileViewModel + ProfileModule

**Files:**
- Create: `app/src/main/java/com/burixer85/aipedia/profile/presentation/ProfileUiState.kt`
- Create: `app/src/main/java/com/burixer85/aipedia/profile/presentation/ProfileViewModel.kt`
- Create: `app/src/main/java/com/burixer85/aipedia/profile/di/ProfileModule.kt`
- Create: `app/src/test/java/com/burixer85/aipedia/profile/presentation/ProfileViewModelTest.kt`

- [ ] **Paso 1: Crear ProfileUiState**

Crea `app/src/main/java/com/burixer85/aipedia/profile/presentation/ProfileUiState.kt`:

```kotlin
package com.burixer85.aipedia.profile.presentation

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(
        val displayName: String,
        val email: String,
        val reviewCount: Int,
        val averageRating: Float?,
        val favoriteCount: Int
    ) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}
```

- [ ] **Paso 2: Escribir el test del ViewModel que falla**

Crea `app/src/test/java/com/burixer85/aipedia/profile/presentation/ProfileViewModelTest.kt`:

```kotlin
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
```

- [ ] **Paso 3: Ejecutar el test para verificar que falla**

```
./gradlew test --tests "com.burixer85.aipedia.profile.presentation.ProfileViewModelTest"
```

Esperado: FAIL — `ProfileViewModel` no existe aun.

- [ ] **Paso 4: Crear ProfileViewModel**

Crea `app/src/main/java/com/burixer85/aipedia/profile/presentation/ProfileViewModel.kt`:

```kotlin
package com.burixer85.aipedia.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burixer85.aipedia.auth.domain.usecase.GetCurrentUserUseCase
import com.burixer85.aipedia.auth.domain.usecase.SignOutUseCase
import com.burixer85.aipedia.ratings.domain.usecase.GetUserReviewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getUserReviewsUseCase: GetUserReviewsUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _signOutEvent = MutableSharedFlow<Unit>(replay = 0)
    val signOutEvent: SharedFlow<Unit> = _signOutEvent.asSharedFlow()

    init {
        getCurrentUserUseCase()
            .onEach { user ->
                if (user == null) {
                    _uiState.value = ProfileUiState.Loading
                    return@onEach
                }
                try {
                    val reviews = getUserReviewsUseCase(user.id)
                    val averageRating = if (reviews.isEmpty()) null
                                        else reviews.map { it.score }.average().toFloat()
                    _uiState.value = ProfileUiState.Success(
                        displayName = user.displayName,
                        email = user.email,
                        reviewCount = reviews.size,
                        averageRating = averageRating,
                        favoriteCount = 0
                    )
                } catch (e: Exception) {
                    _uiState.value = ProfileUiState.Error("No se pudo cargar el perfil")
                }
            }
            .launchIn(viewModelScope)
    }

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
            _signOutEvent.emit(Unit)
        }
    }
}
```

- [ ] **Paso 5: Crear ProfileModule**

Crea `app/src/main/java/com/burixer85/aipedia/profile/di/ProfileModule.kt`:

```kotlin
package com.burixer85.aipedia.profile.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule
```

El modulo esta vacio: `ProfileViewModel` usa `@HiltViewModel` + `@Inject constructor`, por lo que Hilt lo resuelve automaticamente. El modulo existe para mantener la estructura de paquetes consistente.

- [ ] **Paso 6: Ejecutar el test para verificar que pasa**

```
./gradlew test --tests "com.burixer85.aipedia.profile.presentation.ProfileViewModelTest"
```

Esperado: PASS (4 tests)

- [ ] **Paso 7: Commit**

```
git add app/src/main/java/com/burixer85/aipedia/profile/
git add app/src/test/java/com/burixer85/aipedia/profile/
git commit -m "feat: add ProfileViewModel, ProfileUiState, and ProfileModule"
```

---

## Task 3: ProfileScreen composable

**Files:**
- Create: `app/src/main/java/com/burixer85/aipedia/profile/presentation/ProfileScreen.kt`

- [ ] **Paso 1: Crear ProfileScreen.kt**

Crea `app/src/main/java/com/burixer85/aipedia/profile/presentation/ProfileScreen.kt`:

```kotlin
package com.burixer85.aipedia.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.burixer85.aipedia.ui.theme.CatEducacionA
import com.burixer85.aipedia.ui.theme.MdBackground
import com.burixer85.aipedia.ui.theme.MdOnSurfaceMuted
import com.burixer85.aipedia.ui.theme.MdOnSurfaceStrong
import com.burixer85.aipedia.ui.theme.MdOnSurfaceVariant
import com.burixer85.aipedia.ui.theme.MdPrimary
import com.burixer85.aipedia.ui.theme.MdSurfaceContainer

@Composable
fun ProfileScreen(
    onSignOut: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.signOutEvent.collect { onSignOut() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MdBackground)
    ) {
        when (val state = uiState) {
            is ProfileUiState.Loading -> ProfileLoadingContent()
            is ProfileUiState.Success -> ProfileSuccessContent(
                state = state,
                onSignOut = viewModel::signOut
            )
            is ProfileUiState.Error -> ProfileErrorContent(state.message)
        }
    }
}

@Composable
private fun ProfileLoadingContent() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Cargando perfil...", color = MdOnSurfaceVariant, fontSize = 15.sp)
    }
}

@Composable
private fun ProfileErrorContent(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = message,
            color = MdOnSurfaceVariant,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
private fun ProfileSuccessContent(
    state: ProfileUiState.Success,
    onSignOut: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        item { ProfileHeader(state.displayName, state.email) }
        item { Spacer(Modifier.height(16.dp)) }
        item {
            ProfileStatsRow(
                reviewCount = state.reviewCount,
                averageRating = state.averageRating,
                favoriteCount = state.favoriteCount,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        item { Spacer(Modifier.height(12.dp)) }
        item {
            ProfileSettingsSection(modifier = Modifier.padding(horizontal = 16.dp))
        }
        item { Spacer(Modifier.height(16.dp)) }
        item {
            Button(
                onClick = onSignOut,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0x1AFF6464),
                    contentColor = Color(0xFFFF7B7B)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Cerrar sesion",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun ProfileHeader(displayName: String, email: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(colors = listOf(Color(0xFF1C2550), Color(0xFF1C2026)))
            )
            .statusBarsPadding()
            .padding(top = 24.dp, bottom = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(colors = listOf(CatEducacionA, MdPrimary))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = displayName.toInitials(),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C2E61)
                )
            }
            Text(
                text = displayName,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = MdOnSurfaceStrong
            )
            Text(text = email, fontSize = 12.sp, color = MdOnSurfaceVariant)
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MdPrimary.copy(alpha = 0.12f)
            ) {
                Text(
                    text = "Cuenta Google",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MdPrimary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun ProfileStatsRow(
    reviewCount: Int,
    averageRating: Float?,
    favoriteCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(value = reviewCount.toString(), label = "Resenas", modifier = Modifier.weight(1f))
        StatCard(
            value = averageRating?.let { "%.1f".format(it) } ?: "--",
            label = "Rating medio",
            modifier = Modifier.weight(1f)
        )
        StatCard(value = favoriteCount.toString(), label = "Favoritos", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MdSurfaceContainer
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MdPrimary)
            Text(text = label, fontSize = 10.sp, color = MdOnSurfaceMuted)
        }
    }
}

@Composable
private fun ProfileSettingsSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SettingsItem(icon = "Idioma", label = "Idioma", value = "Espanol")
        SettingsItem(icon = "Notif.", label = "Notificaciones", value = "Activadas")
        SettingsItem(icon = "v", label = "Version", value = "1.0.0")
    }
}

@Composable
private fun SettingsItem(icon: String, label: String, value: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MdSurfaceContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = label, fontSize = 13.sp, color = MdOnSurfaceStrong, modifier = Modifier.weight(1f))
            Text(text = value, fontSize = 13.sp, color = MdOnSurfaceVariant)
        }
    }
}

private fun String.toInitials(): String =
    split(" ").filter { it.isNotEmpty() }.take(2).map { it.first().uppercaseChar() }.joinToString("")
```

- [ ] **Paso 2: Compilar para verificar que no hay errores**

```
./gradlew assembleDebug
```

Esperado: BUILD SUCCESSFUL

- [ ] **Paso 3: Commit**

```
git add app/src/main/java/com/burixer85/aipedia/profile/presentation/ProfileScreen.kt
git commit -m "feat: add ProfileScreen composable"
```

---

## Task 4: Navigation refactor — bottom nav compartido + ruta Profile

**Files:**
- Modify: `app/src/main/java/com/burixer85/aipedia/navigation/NavigationRoute.kt`
- Modify: `app/src/main/java/com/burixer85/aipedia/navigation/NavigationHost.kt`
- Modify: `app/src/main/java/com/burixer85/aipedia/home/presentation/HomeScreen.kt`

- [ ] **Paso 1: Anadir ruta Profile a NavigationRoute**

Reemplaza el contenido completo de `app/src/main/java/com/burixer85/aipedia/navigation/NavigationRoute.kt`:

```kotlin
package com.burixer85.aipedia.navigation

sealed class NavigationRoute(val route: String) {
    object Home : NavigationRoute("home")
    object Ai : NavigationRoute("ai")
    object Profile : NavigationRoute("profile")
}
```

- [ ] **Paso 2: Refactorizar NavigationHost**

Reemplaza el contenido completo de `app/src/main/java/com/burixer85/aipedia/navigation/NavigationHost.kt`:

```kotlin
package com.burixer85.aipedia.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.burixer85.aipedia.core.di.SupabaseEntryPoint
import com.burixer85.aipedia.detail.presentation.DetailScreen
import com.burixer85.aipedia.home.presentation.HomeScreen
import com.burixer85.aipedia.profile.presentation.ProfileScreen
import com.burixer85.aipedia.ui.theme.MdOnSurfaceVariant
import com.burixer85.aipedia.ui.theme.MdPrimaryContainer
import dagger.hilt.android.EntryPointAccessors

@Composable
fun NavigationHost(navHostController: NavHostController) {
    val context = LocalContext.current
    val supabase = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            SupabaseEntryPoint::class.java
        ).supabaseClient()
    }

    val backStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomNav = currentRoute?.startsWith(NavigationRoute.Ai.route) != true

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navHostController,
            startDestination = NavigationRoute.Home.route,
        ) {
            composable(NavigationRoute.Home.route) {
                HomeScreen(
                    onAiClick = { aiId ->
                        navHostController.navigate(NavigationRoute.Ai.route + "?aiId=$aiId")
                    }
                )
            }

            composable(NavigationRoute.Profile.route) {
                ProfileScreen(
                    onSignOut = {
                        navHostController.navigate(NavigationRoute.Home.route) {
                            popUpTo(NavigationRoute.Home.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(
                route = NavigationRoute.Ai.route + "?aiId={aiId}",
                arguments = listOf(
                    navArgument(name = "aiId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                ),
                enterTransition = { fadeIn(animationSpec = tween(300)) },
                exitTransition = { fadeOut(animationSpec = tween(300)) },
                popEnterTransition = { fadeIn(animationSpec = tween(300)) },
                popExitTransition = { fadeOut(animationSpec = tween(300)) }
            ) { backStackEntry ->
                val aiId = backStackEntry.arguments?.getString("aiId")
                DetailScreen(
                    aiId = aiId,
                    onBack = { navHostController.popBackStack() },
                    supabase = supabase
                )
            }
        }

        if (showBottomNav) {
            SharedBottomNav(
                currentRoute = currentRoute,
                onItemSelected = { route ->
                    if (route != currentRoute) {
                        navHostController.navigate(route) {
                            popUpTo(NavigationRoute.Home.route) {
                                saveState = true
                                inclusive = false
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

private data class NavItem(
    val route: String,
    val label: String,
    val iconFilled: ImageVector,
    val iconOutlined: ImageVector
)

private val navItems = listOf(
    NavItem(NavigationRoute.Home.route, "Inicio", Icons.Filled.Home, Icons.Outlined.Home),
    NavItem("explore", "Explorar", Icons.Filled.Explore, Icons.Outlined.Explore),
    NavItem(NavigationRoute.Profile.route, "Perfil", Icons.Filled.Person, Icons.Outlined.Person),
)

@Composable
private fun SharedBottomNav(
    currentRoute: String?,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .navigationBarsPadding()
            .padding(bottom = 12.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = Color(0xD91C2026),
            tonalElevation = 0.dp,
            shadowElevation = 20.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                navItems.forEach { item ->
                    NavBarItem(
                        item = item,
                        isActive = currentRoute == item.route,
                        onClick = { onItemSelected(item.route) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun NavBarItem(
    item: NavItem,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (isActive) MdPrimaryContainer else Color.Transparent
    val tint = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer else MdOnSurfaceVariant

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(44.dp)
            .background(bg, RoundedCornerShape(26.dp))
            .then(
                if (item.route != "explore") {
                    Modifier.clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onClick
                    )
                } else Modifier
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isActive) item.iconFilled else item.iconOutlined,
                contentDescription = item.label,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
            if (isActive) {
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = item.label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = tint,
                    letterSpacing = (-0.065).sp
                )
            }
        }
    }
}
```

- [ ] **Paso 3: Eliminar bottom nav de HomeScreen**

En `app/src/main/java/com/burixer85/aipedia/home/presentation/HomeScreen.kt`:

**a)** Elimina la linea que llama al bottom nav dentro de `HomeScreen` (esta dentro del `Box`, al final):
```kotlin
        // Floating bottom navigation
        BottomNav(modifier = Modifier.align(Alignment.BottomCenter))
```

**b)** Elimina las tres funciones privadas al final del archivo: `BottomNav` (composable), `NavItem` (data class) y `NavBarItem` (composable) — lineas 244 a 318 del archivo original.

**c)** Elimina los imports que quedaron sin usar. Busca y elimina estas lineas del bloque de imports:
```kotlin
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Surface
import com.burixer85.aipedia.ui.theme.MdPrimaryContainer
```

- [ ] **Paso 4: Compilar**

```
./gradlew assembleDebug
```

Esperado: BUILD SUCCESSFUL sin errores de compilacion.

- [ ] **Paso 5: Ejecutar todos los tests**

```
./gradlew test
```

Esperado: PASS en todos los tests (incluyendo los nuevos de Task 1 y Task 2).

- [ ] **Paso 6: Commit final**

```
git add app/src/main/java/com/burixer85/aipedia/navigation/NavigationRoute.kt
git add app/src/main/java/com/burixer85/aipedia/navigation/NavigationHost.kt
git add app/src/main/java/com/burixer85/aipedia/home/presentation/HomeScreen.kt
git commit -m "feat: profile screen navigation - lift bottom nav to NavigationHost, add Profile route"
```

---

## Verificacion manual en dispositivo/emulador

Instala con `./gradlew installDebug` y verifica:

- [ ] Bottom nav visible en pantalla Home con "Inicio" activo y resaltado
- [ ] Pulsar "Perfil" navega a la pantalla de perfil con "Perfil" activo en el nav
- [ ] El header muestra iniciales, nombre y email del usuario autenticado
- [ ] Las estadisticas de resenas y rating se cargan (o muestran 0/-- si no hay resenas)
- [ ] "Favoritos" muestra 0
- [ ] Pulsar "Cerrar sesion" navega de vuelta a Home
- [ ] Abrir un detalle de AI oculta el bottom nav
- [ ] Volver del detalle restaura el bottom nav con el item correcto activo
- [ ] El item "Explorar" (centro) no navega — es intencionado por ahora
