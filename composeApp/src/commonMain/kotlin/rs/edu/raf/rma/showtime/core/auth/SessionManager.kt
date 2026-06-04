package rs.edu.raf.rma.showtime.core.auth

import kotlinx.coroutines.flow.Flow
import rs.edu.raf.rma.showtime.core.datastore.TokenStore

class SessionManager(
    private val tokenStore: TokenStore,
) {
    val accessToken: Flow<String?> = tokenStore.accessToken

    suspend fun saveAccessToken(token: String) {
        tokenStore.saveAccessToken(token)
    }

    suspend fun currentAccessToken(): String? = tokenStore.currentAccessToken()

    suspend fun logout() {
        tokenStore.clearAccessToken()
    }

    suspend fun forceLogout() {
        tokenStore.clearAccessToken()
    }
}
