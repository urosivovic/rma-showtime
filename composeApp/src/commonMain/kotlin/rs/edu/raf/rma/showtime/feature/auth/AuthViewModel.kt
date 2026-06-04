package rs.edu.raf.rma.showtime.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import rs.edu.raf.rma.showtime.core.network.toAuthMessage
import rs.edu.raf.rma.showtime.data.repository.AuthRepository

class AuthViewModel(
    private val repository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthScreenState())
    val state: StateFlow<AuthScreenState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<AuthEffect>()
    val effects: SharedFlow<AuthEffect> = _effects.asSharedFlow()

    private var authRequestJob: Job? = null
    private var sessionCheckJob: Job? = null
    private var lastCheckedToken: String? = null

    init {
        observeSession()
    }

    fun onIntent(intent: AuthIntent) {
        when (intent) {
            AuthIntent.LoginSelected -> {
                dispatch(AuthAction.ModeChanged(AuthMode.Login))
            }

            AuthIntent.SignupSelected -> {
                dispatch(AuthAction.ModeChanged(AuthMode.Signup))
            }

            AuthIntent.BackToLandingClicked -> {
                dispatch(AuthAction.ModeChanged(AuthMode.Landing))
            }

            is AuthIntent.FullNameChanged -> {
                dispatch(AuthAction.FullNameChanged(intent.value))
            }

            is AuthIntent.UsernameChanged -> {
                dispatch(AuthAction.UsernameChanged(intent.value))
            }

            is AuthIntent.PasswordChanged -> {
                dispatch(AuthAction.PasswordChanged(intent.value))
            }

            AuthIntent.LoginSubmitted -> {
                login()
            }

            AuthIntent.SignupSubmitted -> {
                signup()
            }

            AuthIntent.LogoutRequested -> {
                logout()
            }
        }
    }

    private fun dispatch(action: AuthAction) {
        _state.update { current ->
            AuthReducer.reduce(current, action)
        }
    }

    private fun sendEffect(effect: AuthEffect) {
        viewModelScope.launch {
            _effects.emit(effect)
        }
    }

    private fun observeSession() {
        viewModelScope.launch {
            repository.accessToken.collectLatest { token ->
                if (token.isNullOrBlank()) {
                    lastCheckedToken = null
                    dispatch(AuthAction.SessionMissing)
                } else if (token != lastCheckedToken) {
                    lastCheckedToken = token
                    verifyCurrentSession()
                }
            }
        }
    }

    private fun verifyCurrentSession() {
        sessionCheckJob?.cancel()
        sessionCheckJob = viewModelScope.launch {
            dispatch(AuthAction.SessionCheckStarted)

            runCatching {
                repository.getCurrentUser()
            }.onSuccess { user ->
                dispatch(AuthAction.SessionAuthenticated(user))
            }.onFailure {
                dispatch(AuthAction.SessionMissing)
                sendEffect(
                    AuthEffect.ShowMessage(
                        "Could not verify session. Please log in again.",
                    ),
                )
            }
        }
    }

    private fun login() {
        if (!validateLogin()) return

        authRequestJob?.cancel()
        authRequestJob = viewModelScope.launch {
            val current = state.value
            dispatch(AuthAction.AuthRequestStarted)

            runCatching {
                repository.login(
                    username = current.username,
                    password = current.password,
                )
            }.onSuccess { user ->
                dispatch(AuthAction.SessionAuthenticated(user))
            }.onFailure { throwable ->
                dispatch(AuthAction.AuthFailed(throwable.toAuthMessage()))
            }
        }
    }

    private fun signup() {
        if (!validateSignup()) return

        authRequestJob?.cancel()
        authRequestJob = viewModelScope.launch {
            val current = state.value
            dispatch(AuthAction.AuthRequestStarted)

            runCatching {
                repository.signup(
                    fullName = current.fullName,
                    username = current.username,
                    password = current.password,
                )
            }.onSuccess { user ->
                dispatch(AuthAction.SessionAuthenticated(user))
            }.onFailure { throwable ->
                dispatch(AuthAction.AuthFailed(throwable.toAuthMessage()))
            }
        }
    }

    private fun logout() {
        authRequestJob?.cancel()
        sessionCheckJob?.cancel()
        authRequestJob = viewModelScope.launch {
            repository.logout()
            dispatch(AuthAction.LoggedOut)
        }
    }

    private fun validateLogin(): Boolean {
        val current = state.value
        val usernameError = current.username.usernameValidationError()
        val passwordError = current.password.passwordValidationError()

        dispatch(
            AuthAction.ValidationFailed(
                fullNameError = null,
                usernameError = usernameError,
                passwordError = passwordError,
            ),
        )

        return usernameError == null && passwordError == null
    }

    private fun validateSignup(): Boolean {
        val current = state.value
        val fullNameError = if (current.fullName.trim().isBlank()) {
            "Full name is required."
        } else {
            null
        }
        val usernameError = current.username.usernameValidationError()
        val passwordError = current.password.passwordValidationError()

        dispatch(
            AuthAction.ValidationFailed(
                fullNameError = fullNameError,
                usernameError = usernameError,
                passwordError = passwordError,
            ),
        )

        return fullNameError == null && usernameError == null && passwordError == null
    }

    private fun String.usernameValidationError(): String? {
        val value = trim()

        return when {
            value.length < 3 -> "Username must have at least 3 characters."
            !value.matches(UsernameRegex) -> "Use only letters, digits, and underscore."
            else -> null
        }
    }

    private fun String.passwordValidationError(): String? =
        if (length < 8) {
            "Password must have at least 8 characters."
        } else {
            null
        }

    private companion object {
        val UsernameRegex = Regex("^[A-Za-z0-9_]+$")
    }
}
