package com.example.mandatoryassignment_birthday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mandatoryassignment_birthday.views.AddBirthdayScreen
import com.example.mandatoryassignment_birthday.views.BirthdayListScreen
import com.example.mandatoryassignment_birthday.views.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        // Destination: Login Screen
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    // Navigate to the birthday list screen on successful login
                    navController.navigate("birthdayList") {
                        // Clear the back stack to prevent going back to the login screen
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // Destination: Birthday List Screen
        composable("birthdayList") {
            BirthdayListScreen(
                onLogout = {
                    navController.navigate("Login") {
                        // Clear the list screen from history so they can't "Go Back" into the app
                        popUpTo("birthdayList") { inclusive = true }
                    }
                },
                onNavigateToAddBirthday = {
                    navController.navigate("addBirthday")
                }
            )
        }

        composable("addBirthday") {
            AddBirthdayScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
