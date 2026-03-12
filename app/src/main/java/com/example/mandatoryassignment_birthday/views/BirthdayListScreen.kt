package com.example.mandatoryassignment_birthday.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mandatoryassignment_birthday.viewmodel.BirthdayViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun BirthdayListScreen(
    viewModel: BirthdayViewModel = koinViewModel()
) {
    val birthdayList by viewModel.birthdays.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchBirthdays()
    }

    Scaffold(
        topBar = {
            Text(
                text = "Upcoming Birthdays",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(birthdayList) { birthday ->
                // This is a single row in the list
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = birthday.name, style = MaterialTheme.typography.titleLarge)
                        Text(text = "Date: ${birthday.birthDayOfMonth}/${birthday.birthMonth} - ${birthday.birthYear}", style = MaterialTheme.typography.bodyMedium)
                        // TODO: Add more UI elements like icons or delete buttons
                    }
                }
            }

            // Show a message if the list is empty
            if (birthdayList.isEmpty()) {
                item {
                    Text(
                        text = "No birthdays found.",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BirthdayListScreenPreview() {
    BirthdayListScreen()
}