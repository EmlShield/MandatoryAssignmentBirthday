package com.example.mandatoryassignment_birthday.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mandatoryassignment_birthday.data.model.Birthday
import com.example.mandatoryassignment_birthday.data.repository.BirthdayRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

class BirthdayViewModel(private val repository: BirthdayRepository) : ViewModel() {

    // Holds the current user's ID
    private var currentUserId: String? = null

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

    // "One-time" event stream for navigation
    private val _navigationEvent = MutableSharedFlow<Boolean>()
    val navigationEvent: SharedFlow<Boolean> = _navigationEvent.asSharedFlow()

    fun clearError() {
        _errorMessage.value = null
    }

    // Refresh the list of birthdays
    private suspend fun performFetch(userId: String) {
        try {
            val result = repository.getBirthdays(userId)
            _birthdays.value = result
            println("DEBUG: Successfully fetched ${result.size} birthdays for $userId")
        } catch (e: Exception) {
            _errorMessage.value = "Failed to load birthdays: ${e.message}"
            println("DEBUG: Error fetching birthdays: ${e.message}")
        }
    }

    // Fetch the birthdays from the repository
    fun fetchBirthdays(userId: String) {
        // Save the user ID for later use
        currentUserId = userId

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                performFetch(userId)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load birthdays: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Add a new birthday
    fun addBirthday(userId: String, name: String, year: Int, month: Int, day: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // Create a new Birthday object
                val newBirthday = Birthday(
                    id = 0,
                    userId = userId,
                    name = name,
                    birthYear = year,
                    birthMonth = month,
                    birthDayOfMonth = day,
                    description = null,
                    pictureUrl = null,
                    age = null
                )

                if (repository.addBirthday(newBirthday)) {
                    performFetch(userId) // Refresh the list to show the new item
                    _navigationEvent.emit(true)
                } else {
                    _errorMessage.value = "Could not add birthday"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Delete a birthday
    fun deleteBirthday(id: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // Delete the birthday
                val success = repository.deleteBirthday(id)
                if (success) {
                    performFetch(currentUserId ?: "") // Refresh the list
                } else {
                    _errorMessage.value = "Could not delete birthday"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    //
    fun getBirthdayById(id: Int): Birthday? {
        val found = _birthdays.value.find { it.id == id }
        if (found == null) {
            println("DEBUG: Birthday with ID $id not found in list. List size: ${_birthdays.value.size}")
        }
        return found
    }

    fun updateBirthday(id: Int, name: String, year: Int, month: Int, day: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val existing = getBirthdayById(id)
                if (existing != null) {
                    val updatedBirthday = existing.copy(
                        name = name,
                        birthYear = year,
                        birthMonth = month,
                        birthDayOfMonth = day
                    )
                    if (repository.updateBirthday(id, updatedBirthday)) {
                        currentUserId?.let { performFetch(it) }
                        _navigationEvent.emit(true)
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}