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
                    _uiState.value = ProfileUiState.NotLoggedIn
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
