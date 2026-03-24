package com.example.mandatoryassignment_birthday.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mandatoryassignment_birthday.viewmodel.AuthViewModel
import com.example.mandatoryassignment_birthday.viewmodel.BirthdayViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthdayDetailsScreen(
    birthdayId: Int,
    onBack: () -> Unit,
    viewModel: BirthdayViewModel = koinViewModel(),
    authViewModel: AuthViewModel = koinViewModel()
) {
    val user by authViewModel.userState.collectAsState()
    val birthdays by viewModel.birthdays.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Ensure data is loaded
    LaunchedEffect(user, birthdays) {
        val email = user?.email
        if (email != null && birthdays.isEmpty()) {
            viewModel.fetchBirthdays(email)
        }
    }

    // Get specific birthday from the list
    val birthday = viewModel.getBirthdayById(birthdayId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading && birthday == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (errorMessage != null && birthday == null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp).align(Alignment.Center)
                )
            } else if (birthday != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = birthday.name, style = MaterialTheme.typography.headlineLarge)

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    DetailRow(label = "Date of Birth", value = "${birthday.birthDayOfMonth}/${birthday.birthMonth}-${birthday.birthYear}")

                    DetailRow(label = "Current Age", value = "${birthday.age ?: "N/A"} years old.")

                    DetailRow(label = "Remarks", value = birthday.description ?: "No remarks provided.")

                    DetailRow(label = "Owner (Email)", value = birthday.userId)
                }
            } else {
                Text(
                    "Birthday not found.",
                    modifier = Modifier.padding(16.dp).align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}