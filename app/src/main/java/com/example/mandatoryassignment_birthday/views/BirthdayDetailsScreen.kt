package com.example.mandatoryassignment_birthday.views

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mandatoryassignment_birthday.R
import com.example.mandatoryassignment_birthday.data.model.Birthday
import com.example.mandatoryassignment_birthday.viewmodel.AuthViewModel
import com.example.mandatoryassignment_birthday.viewmodel.BirthdayViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun BirthdayDetailsScreen(
    birthdayId: Int,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit,
    viewModel: BirthdayViewModel = koinViewModel(),
    authViewModel: AuthViewModel = koinViewModel()
) {
    val user by authViewModel.userState.collectAsState()
    val birthdays by viewModel.birthdays.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Find the birthday in the list so it updates when the list updates
    val birthday = remember(birthdays, birthdayId) {
        birthdays.find { it.id == birthdayId }
    }

    // Ensure data is loaded
    LaunchedEffect(user, birthdays) {
        val email = user?.email
        if (email != null && birthdays.isEmpty()) {
            viewModel.fetchBirthdays(email)
        }
    }

    BirthdayDetailsContent(
        birthday = birthday,
        isLoading = isLoading && birthday == null,
        errorMessage = errorMessage,
        onBack = onBack,
        onEdit = { onEdit(birthdayId) },
        onDelete = {
            viewModel.deleteBirthday(birthdayId)
            onBack()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthdayDetailsContent(
    birthday: Birthday?,
    isLoading: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") } },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete this birthday? This action cannot be undone.") }
        )
    }

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
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (errorMessage != null && birthday == null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp).align(Alignment.Center)
                )
            } else if (birthday != null) {
                if (isLandscape) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        AsyncImage(
                            model = birthday.pictureUrl ?: "https://placehold.jp/24/cccccc/ffffff/150x150.png?text=No%20Image",
                            contentDescription = "Birthday Image",
                            placeholder = painterResource(R.drawable.ic_launcher_foreground),
                            error = painterResource(R.drawable.ic_launcher_foreground),
                            modifier = Modifier
                                .weight(1f)
                                .height(300.dp)
                                .clip(MaterialTheme.shapes.medium),
                            contentScale = ContentScale.Crop
                        )

                        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(text = birthday.name, style = MaterialTheme.typography.headlineLarge)
                            HorizontalDivider()
                            DetailRow(label = "Date", value = "${birthday.birthDayOfMonth}/${birthday.birthMonth}-${birthday.birthYear}")
                            DetailRow(label = "Current Age", value = "${birthday.displayAge ?: "N/A"} years old.")
                            DetailRow(label = "Remarks", value = birthday.description ?: "No remarks provided.")
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AsyncImage(
                            model = birthday.pictureUrl ?: "https://placehold.jp/24/cccccc/ffffff/150x150.png?text=No%20Image",
                            contentDescription = "Birthday Image",
                            placeholder = painterResource(R.drawable.ic_launcher_foreground),
                            error = painterResource(R.drawable.ic_launcher_foreground),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .clip(MaterialTheme.shapes.medium),
                            contentScale = ContentScale.Crop
                        )

                        Text(text = birthday.name, style = MaterialTheme.typography.headlineLarge)

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        DetailRow(label = "Date of Birth", value = "${birthday.birthDayOfMonth}/${birthday.birthMonth}-${birthday.birthYear}")
                        DetailRow(label = "Current Age", value = "${birthday.displayAge} years old.")
                        DetailRow(label = "Remarks", value = birthday.description ?: "No remarks provided.")
                    }
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

@Preview(showBackground = true)
@Composable
fun BirthdayDetailsScreenPreview() {
    val sampleBirthday = Birthday(
        id = 1,
        userId = "test@test.com",
        name = "Emil Skjold Larsen",
        birthYear = 1996,
        birthMonth = 10,
        birthDayOfMonth = 2,
        description = "Myself",
        pictureUrl = "https://placehold.jp/24/cccccc/ffffff/150x150.png",
        age = 26
    )
    MaterialTheme {
        BirthdayDetailsContent(
            birthday = sampleBirthday,
            isLoading = false,
            errorMessage = null,
            onBack = {},
            onEdit = {},
            onDelete = {}
        )
    }
}
