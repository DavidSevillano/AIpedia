package com.burixer85.aipedia.auth.domain.usecase

import com.burixer85.aipedia.auth.domain.repository.AuthRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke() = authRepository.signOut()
}
