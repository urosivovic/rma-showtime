package rs.edu.raf.rma.showtime.core.auth

import rs.edu.raf.rma.showtime.core.network.isUnauthorizedResponse

class AuthenticatedRequestRunner(
    private val sessionManager: SessionManager,
) {
    suspend fun <T> run(block: suspend (authorizationHeader: String) -> T): T {
        val token = sessionManager.currentAccessToken() ?: throw AuthRequiredException()

        return try {
            block("Bearer $token")
        } catch (throwable: Throwable) {
            if (throwable.isUnauthorizedResponse()) {
                sessionManager.forceLogout()
            }
            throw throwable
        }
    }
}
