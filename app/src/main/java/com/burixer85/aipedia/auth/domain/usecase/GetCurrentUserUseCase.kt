package com.burixer85.aipedia.auth.domain.usecase

import com.burixer85.aipedia.auth.domain.model.AuthUser
import com.burixer85.aipedia.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke(): Flow<AuthUser?> = authRepository.getCurrentUser()
}
