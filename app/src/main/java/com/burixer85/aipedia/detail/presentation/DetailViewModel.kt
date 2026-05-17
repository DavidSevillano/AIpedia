package com.burixer85.aipedia.detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burixer85.aipedia.auth.domain.model.AuthUser
import com.burixer85.aipedia.auth.domain.usecase.GetCurrentUserUseCase
import com.burixer85.aipedia.core.domain.model.Ai
import com.burixer85.aipedia.detail.domain.usecase.GetAiUseCase
import com.burixer85.aipedia.ratings.domain.model.RatingSummary
import com.burixer85.aipedia.ratings.domain.model.Review
import com.burixer85.aipedia.ratings.domain.usecase.GetRatingSummaryUseCase
import com.burixer85.aipedia.ratings.domain.usecase.GetReviewsUseCase
import com.burixer85.aipedia.ratings.domain.usecase.GetUserRatingUseCase
import com.burixer85.aipedia.ratings.domain.usecase.SubmitRatingUseCase
import com.burixer85.aipedia.ratings.domain.usecase.SubmitReviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getAiUseCase: GetAiUseCase,
    private val getRatingSummaryUseCase: GetRatingSummaryUseCase,
    private val getReviewsUseCase: GetReviewsUseCase,
    private val getUserRatingUseCase: GetUserRatingUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val submitRatingUseCase: SubmitRatingUseCase,
    private val submitReviewUseCase: SubmitReviewUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailScreenUI(isLoading = true))
    val uiState = _uiState.asStateFlow()

    fun loadAiDetails(id: String, deviceId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val ai = getAiUseCase(UUID.fromString(id))
            _uiState.update { it.copy(ai = ai, isLoading = false) }
        }
        viewModelScope.launch {
            getCurrentUserUseCase().collect { user ->
                _uiState.update { it.copy(currentUser = user) }
            }
        }
        viewModelScope.launch {
            try {
                val summary = getRatingSummaryUseCase(id)
                _uiState.update { it.copy(ratingSummary = summary) }
            } catch (_: Exception) {}
        }
        viewModelScope.launch {
            try {
                val reviews = getReviewsUseCase(id)
                _uiState.update { it.copy(reviews = reviews) }
            } catch (_: Exception) {}
        }
        viewModelScope.launch {
            try {
                val rating = getUserRatingUseCase(id, deviceId)
                _uiState.update { it.copy(userRating = rating ?: 0) }
            } catch (_: Exception) {}
        }
    }

    fun submitRating(aiId: String, deviceId: String, score: Int) {
        _uiState.update { it.copy(userRating = score) }
        viewModelScope.launch {
            try {
                submitRatingUseCase(aiId, deviceId, score)
                val summary = getRatingSummaryUseCase(aiId)
                _uiState.update { it.copy(ratingSummary = summary) }
            } catch (_: Exception) {
                _uiState.update { it.copy(userRating = 0) }
            }
        }
    }

    fun submitReview(aiId: String, score: Int, body: String?) {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            try {
                submitReviewUseCase(aiId, user.id, user.displayName, score, body)
                val reviews = getReviewsUseCase(aiId)
                val summary = getRatingSummaryUseCase(aiId)
                _uiState.update { it.copy(reviews = reviews, ratingSummary = summary) }
            } catch (_: Exception) {}
        }
    }
}

data class DetailScreenUI(
    val ai: Ai? = null,
    val isLoading: Boolean = false,
    val ratingSummary: RatingSummary? = null,
    val reviews: List<Review> = emptyList(),
    val currentUser: AuthUser? = null,
    val userRating: Int = 0,
)
