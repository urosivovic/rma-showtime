package rs.edu.raf.rma.showtime.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuthGateLoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Text(
                text = "Checking session...",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
fun AuthScreen(
    state: AuthScreenState,
    onIntent: (AuthIntent) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        when (state.mode) {
            AuthMode.Landing -> AuthLandingContent(onIntent = onIntent)
            AuthMode.Login -> AuthFormContent(
                state = state,
                title = "Log in",
                primaryActionLabel = "Log in",
                onPrimaryAction = {
                    onIntent(AuthIntent.LoginSubmitted)
                },
                onIntent = onIntent,
            )

            AuthMode.Signup -> AuthFormContent(
                state = state,
                title = "Sign up",
                primaryActionLabel = "Create account",
                onPrimaryAction = {
                    onIntent(AuthIntent.SignupSubmitted)
                },
                onIntent = onIntent,
            )
        }
    }
}

@Composable
private fun AuthLandingContent(
    onIntent: (AuthIntent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            text = "Showtime",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Sign in to continue.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                onIntent(AuthIntent.LoginSelected)
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Log in")
        }
        OutlinedButton(
            onClick = {
                onIntent(AuthIntent.SignupSelected)
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Sign up")
        }
    }
}

@Composable
private fun AuthFormContent(
    state: AuthScreenState,
    title: String,
    primaryActionLabel: String,
    onPrimaryAction: () -> Unit,
    onIntent: (AuthIntent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TextButton(
            onClick = {
                onIntent(AuthIntent.BackToLandingClicked)
            },
        ) {
            Text(text = "Back")
        }

        Text(
            text = title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
        )

        if (state.mode == AuthMode.Signup) {
            OutlinedTextField(
                value = state.fullName,
                onValueChange = { value ->
                    onIntent(AuthIntent.FullNameChanged(value))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Full name") },
                isError = state.fullNameError != null,
                supportingText = {
                    FieldErrorText(message = state.fullNameError)
                },
            )
        }

        OutlinedTextField(
            value = state.username,
            onValueChange = { value ->
                onIntent(AuthIntent.UsernameChanged(value))
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Username") },
            isError = state.usernameError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            supportingText = {
                FieldErrorText(message = state.usernameError)
            },
        )

        OutlinedTextField(
            value = state.password,
            onValueChange = { value ->
                onIntent(AuthIntent.PasswordChanged(value))
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Password") },
            isError = state.passwordError != null,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            supportingText = {
                FieldErrorText(message = state.passwordError)
            },
        )

        if (state.errorMessage != null) {
            Text(
                text = state.errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                lineHeight = 17.sp,
            )
        }

        Button(
            onClick = onPrimaryAction,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading,
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(18.dp),
                )
            } else {
                Text(text = primaryActionLabel)
            }
        }
    }
}

@Composable
private fun FieldErrorText(message: String?) {
    if (message != null) {
        Text(text = message)
    }
}
