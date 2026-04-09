package com.example.mandatoryassignment_birthday.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mandatoryassignment_birthday.viewmodel.AuthViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit, // Callback for successful login
    viewModel: AuthViewModel = koinViewModel()
) {
    // Observe state changes from the ViewModel
    val user by viewModel.userState.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Local state for the text fields
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // New State to toggle between Login and Sign-Up
    var isLoginMode by remember { mutableStateOf(true) }

    // If the user state changes to "not null", trigger the success callback
    LaunchedEffect(user) {
        if (user != null) {
            onLoginSuccess()
        }
    }

    LoginContent(
        email = email,
        onEmailChange = { email = it },
        password = password,
        onPasswordChange = { password = it },
        error = error,
        isLoading = isLoading,
        isLoginMode = isLoginMode,
        onModeChange = { isLoginMode = !isLoginMode },
        onActionClick = {
            // Decide which ViewModel function to call
            if (isLoginMode) {
                viewModel.login(email, password)
            } else {
                viewModel.signUp(email, password)
            }
        }
    )
}

@Composable
fun LoginContent(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    error: String?,
    isLoading: Boolean,
    isLoginMode: Boolean,
    onModeChange: () -> Unit,
    onActionClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Dynamic Text based on mode
        Text(
            text = if (isLoginMode) "Welcome to Birthday App" else "Create Account",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        // Show error message if login fails
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = onActionClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isLoginMode) "Login" else "Sign Up")
            }
        }

        // Button to switch modes
        TextButton(onClick = onModeChange) {
            Text(
                if (isLoginMode) "Don't have an account? Sign Up"
                else "Already have an account? Login"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginContent("test@test.com", {}, "1234", {}, null, false, true, {}, {})
}