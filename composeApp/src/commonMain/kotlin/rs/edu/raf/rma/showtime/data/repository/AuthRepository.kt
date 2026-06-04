package rs.edu.raf.rma.showtime.data.repository

import kotlinx.coroutines.flow.Flow
import rs.edu.raf.rma.showtime.core.auth.AuthUser
import rs.edu.raf.rma.showtime.core.auth.AuthenticatedRequestRunner
import rs.edu.raf.rma.showtime.core.auth.SessionManager
import rs.edu.raf.rma.showtime.data.remote.LoginRequestDto
import rs.edu.raf.rma.showtime.data.remote.ShowtimeApi
import rs.edu.raf.rma.showtime.data.remote.SignupRequestDto
import rs.edu.raf.rma.showtime.data.remote.toDomain

interface AuthRepository {
    val accessToken: Flow<String?>

    suspend fun signup(
        fullName: String,
        username: String,
        password: String,
    ): AuthUser

    suspend fun login(
        username: String,
        password: String,
    ): AuthUser

    suspend fun getCurrentUser(): AuthUser

    suspend fun logout()
}

class AuthRepositoryImpl(
    private val api: ShowtimeApi,
    private val sessionManager: SessionManager,
    private val authenticatedRequestRunner: AuthenticatedRequestRunner,
) : AuthRepository {

    override val accessToken: Flow<String?> = sessionManager.accessToken

    override suspend fun signup(
        fullName: String,
        username: String,
        password: String,
    ): AuthUser {
        val response = api.signup(
            SignupRequestDto(
                fullName = fullName.trim(),
                username = username.trim(),
                password = password,
            ),
        )
        sessionManager.saveAccessToken(response.accessToken)
        return response.user.toDomain()
    }

    override suspend fun login(
        username: String,
        password: String,
    ): AuthUser {
        val response = api.login(
            LoginRequestDto(
                username = username.trim(),
                password = password,
            ),
        )
        sessionManager.saveAccessToken(response.accessToken)
        return response.user.toDomain()
    }

    override suspend fun getCurrentUser(): AuthUser =
        authenticatedRequestRunner.run { authorization ->
            api.getMe(authorization).toDomain()
        }

    override suspend fun logout() {
        sessionManager.logout()
    }
}
