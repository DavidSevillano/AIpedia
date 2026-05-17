package com.burixer85.aipedia.auth.data.repository

import com.burixer85.aipedia.auth.domain.model.AuthUser
import com.burixer85.aipedia.auth.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImp @Inject constructor(
    private val supabase: SupabaseClient
) : AuthRepository {

    override fun getCurrentUser(): Flow<AuthUser?> =
        supabase.auth.sessionStatus.map { status ->
            when (status) {
                is SessionStatus.Authenticated -> {
                    val user = status.session.user
                    AuthUser(
                        id = user?.id ?: "",
                        email = user?.email ?: "",
                        displayName = user?.userMetadata
                            ?.get("full_name")?.toString()?.trim('"')
                            ?: user?.email?.substringBefore("@")
                            ?: ""
                    )
                }
                else -> null
            }
        }

    override suspend fun signOut() {
        supabase.auth.signOut()
    }
}
