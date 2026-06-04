package rs.edu.raf.rma.showtime.core.auth

data class AuthUser(
    val id: Int,
    val username: String,
    val fullName: String,
)

class AuthRequiredException : IllegalStateException("Authentication is required.")
