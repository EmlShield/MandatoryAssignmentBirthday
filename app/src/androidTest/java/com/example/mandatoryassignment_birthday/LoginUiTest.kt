package com.example.mandatoryassignment_birthday

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.mandatoryassignment_birthday.views.LoginContent
import org.junit.Rule
import org.junit.Test

class LoginUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_showsError_whenEmptyFieldsSubmitted() {
        composeTestRule.setContent {
            var wasAttemped by remember { mutableStateOf(false) }
            LoginContent(
                email = "",
                onEmailChange = {},
                password = "",
                onPasswordChange = {},
                error = if (wasAttemped) "Invalid fields" else null,
                isLoading = false,
                isLoginMode = true,
                onModeChange = {},
                onActionClick = { wasAttemped = true }
            )
        }

        composeTestRule.onNodeWithTag("login_button").performClick()

        composeTestRule.onNodeWithText("Invalid fields").assertExists()
    }

    @Test
    fun loginScreen_canTypeEmailAndPassword() {
        composeTestRule.setContent {
            var emailState by remember { mutableStateOf("") }
            var passwordState by remember { mutableStateOf("") }

            LoginContent(
                email = emailState,
                onEmailChange = { emailState = it },
                password = passwordState,
                onPasswordChange = { passwordState = it },
                error = null,
                isLoading = false,
                isLoginMode = true,
                onModeChange = {},
                onActionClick = {}
            )
        }

        composeTestRule.onNodeWithTag("email_field").performTextInput("test@test.com")
        composeTestRule.onNodeWithTag("password_field").performTextInput("123456")

        composeTestRule.onNodeWithTag("email_field").assertTextContains("test@test.com")
        composeTestRule.onNodeWithTag("password_field").assertTextContains("123456")
    }

    @Test
    fun loginScreen_switchesBetweenLoginAndSignUp() {
        composeTestRule.setContent {
            var isLoginMode by remember { mutableStateOf(true) }

            LoginContent(
                email = "",
                onEmailChange = {},
                password = "",
                onPasswordChange = {},
                error = null,
                isLoading = false,
                isLoginMode = isLoginMode,
                onModeChange = { isLoginMode = !isLoginMode },
                onActionClick = {}
            )
        }

        // Starts in Login mode
        composeTestRule.onNodeWithText("Welcome to Birthday App").assertIsDisplayed()
        composeTestRule.onNodeWithText("Login").assertIsDisplayed()

        // Switch to Sign Up
        composeTestRule.onNodeWithText("Don't have an account? Sign Up").performClick()

        // Verify Sign Up mode
        composeTestRule.onNodeWithText("Create Account").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign Up").assertIsDisplayed()
    }

    @Test
    fun loginScreen_showsLoadingIndicator() {
        composeTestRule.setContent {
            LoginContent(
                email = "",
                onEmailChange = {},
                password = "",
                onPasswordChange = {},
                error = null,
                isLoading = true,
                isLoginMode = true,
                onModeChange = {},
                onActionClick = {}
            )
        }

        composeTestRule.onNodeWithTag("loading_indicator").assertIsDisplayed()
        composeTestRule.onNodeWithTag("login_button").assertDoesNotExist()
    }
}