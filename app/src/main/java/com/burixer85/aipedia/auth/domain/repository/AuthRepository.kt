package com.burixer85.aipedia.auth.domain.repository

import com.burixer85.aipedia.auth.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getCurrentUser(): Flow<AuthUser?>
    suspend fun signOut()
}
