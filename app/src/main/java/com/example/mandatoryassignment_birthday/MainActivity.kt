package com.example.mandatoryassignment_birthday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mandatoryassignment_birthday.views.AddBirthdayScreen
import com.example.mandatoryassignment_birthday.views.BirthdayDetailsScreen
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
                    navController.navigate("login") {
                        // Clear the list screen from history so they can't "Go Back" into the app
                        popUpTo("birthdayList") { inclusive = true }
                    }
                },
                onEditBirthday = { id ->
                    if (id == -1) {
                        navController.navigate("birthdayForm") // Add Mode
                    } else {
                        navController.navigate("birthdayForm?birthdayId=$id") // Edit Mode
                    }
                },
                // Handle Details Navigation
                onSeeDetails = { id ->
                    navController.navigate("birthdayDetails/$id")
                }
            )
        }

        composable(
            route = "birthdayForm?birthdayId={birthdayId}",
            arguments = listOf(navArgument("birthdayId") {
                type = NavType.IntType
                defaultValue = -1
            })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("birthdayId")

            AddBirthdayScreen(
                birthdayId = if (id == -1) null else id,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            "birthdayDetails/{birthdayId}",
            arguments = listOf(navArgument("birthdayId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("birthdayId") ?: -1

            // Call screen
            BirthdayDetailsScreen(
                birthdayId = id,
                onBack = { navController.popBackStack() },
                onEdit = { birthdayId ->
                    navController.navigate("birthdayForm?birthdayId=$birthdayId")
                }
            )
        }
    }
}
