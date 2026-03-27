package com.example.mandatoryassignment_birthday.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mandatoryassignment_birthday.data.model.Birthday
import com.example.mandatoryassignment_birthday.data.model.SortOrder
import com.example.mandatoryassignment_birthday.viewmodel.AuthViewModel
import com.example.mandatoryassignment_birthday.viewmodel.BirthdayViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthdayListScreen(
    // Ask Koin to provide the ViewModels
    birthdayViewModel: BirthdayViewModel = koinViewModel(),
    authViewModel: AuthViewModel = koinViewModel(),
    onLogout: () -> Unit, // Callback for navigation
    onEditBirthday: (Int) -> Unit,
    onSeeDetails: (Int) -> Unit
) {
    // Collect the list of birthdays from the ViewModel as state
    val birthdayList by birthdayViewModel.birthdays.collectAsState()
    val user by authViewModel.userState.collectAsState()

    // Observe loading & error state
    val isLoading by birthdayViewModel.isLoading.collectAsState()
    val errorMessage by birthdayViewModel.errorMessage.collectAsState()

    val sortOrder by birthdayViewModel.sortOrder.collectAsState()
    val query by birthdayViewModel.filterQuery.collectAsState()

    // Fetch the birthdays when the screen is first displayed
    LaunchedEffect(user) {
        val email = user?.email
        if (email != null) {
            birthdayViewModel.fetchBirthdays(email)
        } else if (user == null) {
            onLogout()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upcoming Birthdays") },
                actions = {
                    // Logout button with Icon and Text
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { authViewModel.logout() }
                            .padding(end = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout"
                        )
                        Text(
                            text = "Log out",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onEditBirthday(-1) // -1 indicates a new birthday
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            FilterSortBar(
                query = query,
                onQueryChange = { birthdayViewModel.setFilterQuery(it) },
                currentSortOrder = sortOrder,
                onSortChange = { birthdayViewModel.setSortOrder(it) }
            )
            // Handle the different UI states
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else if (errorMessage != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                        Button(onClick = {
                            birthdayViewModel.fetchBirthdays(
                                userId = user?.email ?: ""
                            )
                        }) {
                            Text("Retry")
                        }
                    }
                } else {
                    // Only show the content if not loading and no error
                    BirthdayListContent(
                        birthdays = birthdayList,
                        onDeleteClick = { id -> birthdayViewModel.deleteBirthday(id) },
                        onEditClick = { id -> onEditBirthday(id) },
                        onCardClick = { id -> onSeeDetails(id) }
                    )
                }
            }
        }
    }
}

@Composable
fun BirthdayListContent(
    birthdays: List<Birthday>,
    onDeleteClick: (Int) -> Unit,
    onEditClick: (Int) -> Unit,
    onCardClick: (Int) -> Unit,
    modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        // Display the list of birthdays in a LazyColumn
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(birthdays) { birthday ->
                // This is a single row in the list
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { onCardClick(birthday.id) } // Clicking the card = Detail screen
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = birthday.name, style = MaterialTheme.typography.titleLarge)
                            Text(text = "Date: ${birthday.birthDayOfMonth}/${birthday.birthMonth} - ${birthday.birthYear}", style = MaterialTheme.typography.bodyMedium)
                        }

                        val days = birthday.daysUntilNextBirthday()
                        Text(
                            text = if (days == 365L) "TODAY" else "$days days to go",
                            color = if (days <= 7) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.bodySmall
                        )

                        // Edit button
                        IconButton(onClick = { onEditClick(birthday.id) }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit"
                            )
                        }

                        // Delete button
                        IconButton(onClick = { onDeleteClick(birthday.id) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            // Show a message if the list is empty
            if (birthdays.isEmpty()) {
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

@Composable
fun FilterSortBar(
    query: String,
    onQueryChange: (String) -> Unit,
    currentSortOrder: SortOrder,
    onSortChange: (SortOrder) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 2.dp, // Makes it look "pinned" over the list
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                label = { Text("Filter by Name or Age") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SortChip("Name", currentSortOrder == SortOrder.NAME) { onSortChange(SortOrder.NAME) }
                SortChip("Date", currentSortOrder == SortOrder.DATE) { onSortChange(SortOrder.DATE) }
                SortChip("Age", currentSortOrder == SortOrder.AGE) { onSortChange(SortOrder.AGE) }
            }
        }
    }
}

@Composable
fun SortChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = if (isSelected) {
            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
        } else null
    )
}