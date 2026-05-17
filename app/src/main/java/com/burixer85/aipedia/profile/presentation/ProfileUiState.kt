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
