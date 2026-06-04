package rs.edu.raf.rma.showtime.feature.auth

object AuthReducer {
    fun reduce(
        state: AuthScreenState,
        action: AuthAction,
    ): AuthScreenState =
        when (action) {
            AuthAction.SessionCheckStarted -> state.copy(
                gateState = AuthGateState.Checking,
                isLoading = false,
                errorMessage = null,
            )

            AuthAction.SessionMissing -> state.copy(
                gateState = AuthGateState.Unauthenticated,
                isLoading = false,
                errorMessage = null,
            )

            is AuthAction.SessionAuthenticated -> state.copy(
                gateState = AuthGateState.Authenticated(action.user),
                isLoading = false,
                errorMessage = null,
                fullNameError = null,
                usernameError = null,
                passwordError = null,
            )

            is AuthAction.ModeChanged -> state.copy(
                mode = action.mode,
                fullName = "",
                username = "",
                password = "",
                fullNameError = null,
                usernameError = null,
                passwordError = null,
                errorMessage = null,
                isLoading = false,
            )

            is AuthAction.FullNameChanged -> state.copy(
                fullName = action.value,
                fullNameError = null,
                errorMessage = null,
            )

            is AuthAction.UsernameChanged -> state.copy(
                username = action.value,
                usernameError = null,
                errorMessage = null,
            )

            is AuthAction.PasswordChanged -> state.copy(
                password = action.value,
                passwordError = null,
                errorMessage = null,
            )

            AuthAction.AuthRequestStarted -> state.copy(
                isLoading = true,
                errorMessage = null,
                fullNameError = null,
                usernameError = null,
                passwordError = null,
            )

            is AuthAction.AuthFailed -> state.copy(
                gateState = AuthGateState.Unauthenticated,
                isLoading = false,
                errorMessage = action.message,
            )

            is AuthAction.ValidationFailed -> state.copy(
                isLoading = false,
                fullNameError = action.fullNameError,
                usernameError = action.usernameError,
                passwordError = action.passwordError,
            )

            AuthAction.LoggedOut -> state.copy(
                gateState = AuthGateState.Unauthenticated,
                mode = AuthMode.Landing,
                fullName = "",
                username = "",
                password = "",
                fullNameError = null,
                usernameError = null,
                passwordError = null,
                errorMessage = null,
                isLoading = false,
            )
        }
}
