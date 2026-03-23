package com.example.mandatoryassignment_birthday.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mandatoryassignment_birthday.viewmodel.AuthViewModel
import com.example.mandatoryassignment_birthday.viewmodel.BirthdayViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBirthdayScreen(
    birthdayId: Int? = null,
    onBack: () -> Unit,
    viewModel: BirthdayViewModel = koinViewModel(),
    authViewModel: AuthViewModel = koinViewModel()
) {
    var name by remember { mutableStateOf("") }
    val user by authViewModel.userState.collectAsState()

    // DatePicker state
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    // Helper to format the display date
    val selectedDateText = datePickerState.selectedDateMillis?.let {
        val date = Date(it)
        val calendar = Calendar.getInstance()
        calendar.time = date
        // Months in Calendar are 0-based, so we add 1
        "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
    } ?: "Select Date"

    LaunchedEffect(birthdayId) {
        if (birthdayId != null && birthdayId != -1) {
            val existing = viewModel.getBirthdayById(birthdayId)
            if (existing != null) {
                name = existing.name

                // Set the DatePicker to the existing date
                val calendar = Calendar.getInstance()
                calendar.set(existing.birthYear, existing.birthMonth - 1, existing.birthDayOfMonth)

                // Update the DatePicker state
                datePickerState.selectedDateMillis = calendar.timeInMillis
            }
        }
    }

    LaunchedEffect(Unit) {
        // Clear the error when the screen is displayed
        viewModel.clearError()

        viewModel.navigationEvent.collect { isDone ->
            if (isDone) {
                onBack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (birthdayId == null || birthdayId == -1) "Add Birthday" else "Edit Birthday") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            // Read-only field that opens the DatePicker
            OutlinedTextField(
                value = selectedDateText,
                onValueChange = { },
                label = { Text("Date of Birth") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }, // Open the DatePicker when clicked
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                    }
                },
                // Ensures the field looks clickable
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    // Extract the date from the picker
                    val millis = datePickerState.selectedDateMillis
                    val currentUserEmail = user?.email

                    if (millis != null && currentUserEmail != null) {
                        val calendar = Calendar.getInstance().apply { timeInMillis = millis }
                        val year = calendar.get(Calendar.YEAR)
                        val month = calendar.get(Calendar.MONTH) + 1
                        val day = calendar.get(Calendar.DAY_OF_MONTH)

                        if (birthdayId == null || birthdayId == -1) {
                            // Add Mode
                            viewModel.addBirthday(
                                currentUserEmail,
                                name,
                                year,
                                month,
                                day
                            )
                        } else {
                            // Edit Mode
                            viewModel.updateBirthday(
                                birthdayId,
                                name,
                                year,
                                month,
                                day
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                // Only enable the button if both fields are filled
                enabled = name.isNotBlank() && datePickerState.selectedDateMillis != null && user != null
            ) {
                Text(if (birthdayId == null || birthdayId == -1) "Save Birthday" else "Update Birthday")
            }
        }

        // Show the DatePicker dialog when requested
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}