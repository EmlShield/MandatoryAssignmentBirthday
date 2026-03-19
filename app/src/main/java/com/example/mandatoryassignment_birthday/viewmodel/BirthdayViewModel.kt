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

    // This function triggers the API call
    fun fetchBirthdays() {
        viewModelScope.launch {
            try {
                val result = repository.getBirthdays()
                _birthdays.value = result // Update the StateFlow with the new data
            } catch (e: Exception) {
                // TODO: Handle errors for the UI
            }
        }
    }
}