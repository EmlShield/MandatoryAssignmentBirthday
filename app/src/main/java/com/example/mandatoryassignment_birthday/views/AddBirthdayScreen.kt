package com.example.mandatoryassignment_birthday.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBirthdayScreen(
    birthdayId: Int? = null,
    onBack: () -> Unit,
    viewModel: BirthdayViewModel = koinViewModel(),
    authViewModel: AuthViewModel = koinViewModel()
) {
    var name by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("") }

    val user by authViewModel.userState.collectAsState()
    
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val birthdays by viewModel.birthdays.collectAsState()

    // DatePicker state
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    // Helper to format the display date using LocalDate
    val selectedDateText = datePickerState.selectedDateMillis?.let {
        val date = Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
        date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    } ?: "Select Date"

    var wasAttempted by remember { mutableStateOf(false) }
    val isNameValid = name.isNotBlank()
    val isDateValid = datePickerState.selectedDateMillis != null

    // Fetch data if we are editing and the list is empty
    LaunchedEffect(user, birthdays) {
        val email = user?.email
        if (email != null && birthdays.isEmpty()) {
            viewModel.fetchBirthdays(email)
        }
    }

    LaunchedEffect(birthdays, birthdayId) {
        if (birthdayId != null && birthdayId != -1) {
            val existing = viewModel.getBirthdayById(birthdayId)
            if (existing != null) {
                name = existing.name
                remarks = existing.description ?: ""
                val localDate = LocalDate.of(existing.birthYear, existing.birthMonth, existing.birthDayOfMonth)
                datePickerState.selectedDateMillis = localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            }
        }
    }

    LaunchedEffect(Unit) {
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                isError = wasAttempted && !isNameValid,
                supportingText = {
                    if (wasAttempted && !isNameValid) {
                        Text("Name cannot be empty", color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            OutlinedTextField(
                value = selectedDateText,
                onValueChange = { },
                label = { Text("Date of Birth") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !isLoading) { showDatePicker = true },
                isError = wasAttempted && !isDateValid,
                supportingText = {
                    if (wasAttempted && !isDateValid) {
                        Text("Please select a date", color = MaterialTheme.colorScheme.error)
                    }
                },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }, enabled = !isLoading) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                    }
                },
                enabled = true
            )

            OutlinedTextField(
                value = remarks,
                onValueChange = { remarks = it },
                label = { Text("Remarks (Optional)") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                singleLine = false,
                maxLines = 4,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    wasAttempted = true
                    if (isNameValid && isDateValid) {
                        val millis = datePickerState.selectedDateMillis
                        val currentUserEmail = user?.email

                        if (millis != null && currentUserEmail != null) {
                            val localDate = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                            val year = localDate.year
                            val month = localDate.monthValue
                            val day = localDate.dayOfMonth

                            if (birthdayId == null || birthdayId == -1) {
                                viewModel.addBirthday(currentUserEmail, name, year, month, day, remarks)
                            } else {
                                viewModel.updateBirthday(birthdayId, name, year, month, day, remarks)
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(if (birthdayId == null || birthdayId == -1) "Save Birthday" else "Update Birthday")
                }
            }
        }

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
