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
import com.example.mandatoryassignment_birthday.viewmodel.BirthdayViewModel
import com.example.mandatoryassignment_birthday.views.AddBirthdayScreen
import com.example.mandatoryassignment_birthday.views.BirthdayDetailsScreen
import com.example.mandatoryassignment_birthday.views.BirthdayListScreen
import com.example.mandatoryassignment_birthday.views.LoginScreen
import org.koin.androidx.compose.koinViewModel

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

    val birthdayViewModel: BirthdayViewModel = koinViewModel()

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("birthdayList") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("birthdayList") {
            BirthdayListScreen(
                birthdayViewModel = birthdayViewModel,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("birthdayList") { inclusive = true }
                    }
                },
                onEditBirthday = { id ->
                    if (id == -1) {
                        navController.navigate("birthdayForm")
                    } else {
                        navController.navigate("birthdayForm?birthdayId=$id")
                    }
                },
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
                viewModel = birthdayViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            "birthdayDetails/{birthdayId}",
            arguments = listOf(navArgument("birthdayId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("birthdayId") ?: -1

            BirthdayDetailsScreen(
                birthdayId = id,
                viewModel = birthdayViewModel,
                onBack = { navController.popBackStack() },
                onEdit = { birthdayId ->
                    navController.navigate("birthdayForm?birthdayId=$birthdayId")
                }
            )
        }
    }
}
