package com.example.mandatoryassignment_birthday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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

    NavHost(navController = navController, startDestination = "birthdayList") {

        composable("birthdayList") {
            BirthdayListScreen()
        }

        composable("login") {
            LoginScreen()
        }
    }
}
