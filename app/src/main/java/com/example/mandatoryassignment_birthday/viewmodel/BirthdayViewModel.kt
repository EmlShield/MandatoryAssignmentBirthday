package com.example.mandatoryassignment_birthday.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mandatoryassignment_birthday.data.model.Birthday
import com.example.mandatoryassignment_birthday.data.repository.BirthdayRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

class BirthdayViewModel(private val repository: BirthdayRepository) : ViewModel() {

    // This is the private "stream" where we update the date
    private val _birthdays = MutableStateFlow<List<Birthday>>(emptyList())

    // This is the public "stream" that the UI reads from
    val birthdays: StateFlow<List<Birthday>> = _birthdays.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // This function triggers the API call
    fun fetchBirthdays() {
        viewModelScope.launch {
            try {
                val result = repository.getBirthdays()
                _birthdays.value = result // Update the StateFlow with the new data
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load birthdays: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addBirthday(name: String, year: Int, month: Int, day: Int) {
        viewModelScope.launch {
            _isLoading.value = true

            // Create a new Birthday object
            val newBirthday = Birthday(
                id = 0,
                userId = "", // TODO: Get from AuthViewModel later
                name = name,
                birthYear = year,
                birthMonth = month,
                birthDayOfMonth = day,
                description = "",
                pictureUrl = "",
                age = 0
            )

            val success = repository.addBirthday(newBirthday)
            if (success) {
                fetchBirthdays() // Refresh the list after adding
            } else {
                _errorMessage.value = "Could not add birthday"
            }
            _isLoading.value = false
        }
    }
}