package com.example.mandatoryassignment_birthday.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mandatoryassignment_birthday.viewmodel.BirthdayViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthdayDetailsScreen(
    birthdayId: Int,
    onBack: () -> Unit,
    viewModel: BirthdayViewModel = koinViewModel()
) {
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (birthday != null) {
                Text(text = birthday.name, style = MaterialTheme.typography.headlineLarge)

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Detail Items
                DetailRow(label = "Date of Birth", value = "${birthday.birthDayOfMonth}/${birthday.birthMonth}-${birthday.birthYear}")

                DetailRow(label = "Current Age", value = "${birthday.age ?: "N/A"} years old.")

                DetailRow(label = "Remarks", value = birthday.description ?: "No remarks provided.")

                DetailRow(label = "Owner (Email)", value = birthday.userId)

                // TODO: Add an image display here if pictureUrl is not null
            } else {
                Text("Error: Birthday details could not be found.")
            }
        }
    }
}

// Helper composable to keep UI consistent
@Composable
fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}