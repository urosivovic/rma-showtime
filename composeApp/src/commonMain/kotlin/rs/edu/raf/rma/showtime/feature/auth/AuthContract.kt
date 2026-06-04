package rs.edu.raf.rma.showtime.feature.auth

import rs.edu.raf.rma.showtime.core.auth.AuthUser
import rs.edu.raf.rma.showtime.core.mvi.UiAction
import rs.edu.raf.rma.showtime.core.mvi.UiEffect
import rs.edu.raf.rma.showtime.core.mvi.UiIntent
import rs.edu.raf.rma.showtime.core.mvi.UiState

enum class AuthMode {
    Landing,
    Login,
    Signup,
}

sealed interface AuthGateState {
    data object Checking : AuthGateState
    data object Unauthenticated : AuthGateState
    data class Authenticated(val user: AuthUser) : AuthGateState
}

data class AuthScreenState(
    val gateState: AuthGateState = AuthGateState.Checking,
    val mode: AuthMode = AuthMode.Landing,
    val fullName: String = "",
    val username: String = "",
    val password: String = "",
    val fullNameError: String? = null,
    val usernameError: String? = null,
    val passwordError: String? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
) : UiState

sealed interface AuthIntent : UiIntent {
    data object LoginSelected : AuthIntent
    data object SignupSelected : AuthIntent
    data object BackToLandingClicked : AuthIntent
    data object LoginSubmitted : AuthIntent
    data object SignupSubmitted : AuthIntent
    data object LogoutRequested : AuthIntent
    data class FullNameChanged(val value: String) : AuthIntent
    data class UsernameChanged(val value: String) : AuthIntent
    data class PasswordChanged(val value: String) : AuthIntent
}

sealed interface AuthAction : UiAction {
    data object SessionCheckStarted : AuthAction
    data object SessionMissing : AuthAction
    data class SessionAuthenticated(val user: AuthUser) : AuthAction
    data class ModeChanged(val mode: AuthMode) : AuthAction
    data class FullNameChanged(val value: String) : AuthAction
    data class UsernameChanged(val value: String) : AuthAction
    data class PasswordChanged(val value: String) : AuthAction
    data object AuthRequestStarted : AuthAction
    data class AuthFailed(val message: String) : AuthAction
    data class ValidationFailed(
        val fullNameError: String?,
        val usernameError: String?,
        val passwordError: String?,
    ) : AuthAction
    data object LoggedOut : AuthAction
}

sealed interface AuthEffect : UiEffect {
    data class ShowMessage(val message: String) : AuthEffect
}
