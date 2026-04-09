package com.example.mandatoryassignment_birthday.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mandatoryassignment_birthday.data.model.Birthday
import com.example.mandatoryassignment_birthday.data.model.SortOrder
import com.example.mandatoryassignment_birthday.data.model.calculateAge
import com.example.mandatoryassignment_birthday.data.network.NetworkResult
import com.example.mandatoryassignment_birthday.data.repository.BirthdayRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BirthdayViewModel(private val repository: BirthdayRepository) : ViewModel() {

    private var currentUserId: String? = null

    private val _birthdays = MutableStateFlow<List<Birthday>>(emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<Boolean>()
    val navigationEvent: SharedFlow<Boolean> = _navigationEvent.asSharedFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.NAME)
    val sortOrder = _sortOrder.asStateFlow()

    private val _filterQuery = MutableStateFlow("")
    val filterQuery = _filterQuery.asStateFlow()

    val birthdays: StateFlow<List<Birthday>> = combine(_birthdays, _sortOrder, _filterQuery) { list, order, query ->
        val filteredList = if (query.isEmpty()) {
            list
        } else {
            list.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.age?.toString()?.contains(query) == true
            }
        }
        when (order) {
            SortOrder.NAME -> filteredList.sortedBy { it.name.lowercase() }
            SortOrder.DATE -> filteredList.sortedWith(compareBy({ it.birthMonth }, { it.birthDayOfMonth }))
            SortOrder.AGE -> filteredList.sortedByDescending { it.age ?: 0 }

        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSortOrder(order: SortOrder) { _sortOrder.value = order }

    fun setFilterQuery(query: String) { _filterQuery.value = query }

    fun setError(message: String?) {
        _errorMessage.value = message
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private suspend fun performFetch(userId: String) {
        _isLoading.value = true
        when (val result = repository.getBirthdays(userId)) {
            is NetworkResult.Success -> {
                _birthdays.value = result.data
                _errorMessage.value = null
            }
            is NetworkResult.Error -> {
                _errorMessage.value = result.message
            }
            NetworkResult.Loading -> {
                _isLoading.value = true
            }
        }
        _isLoading.value = false
    }

    fun fetchBirthdays(userId: String) {
        currentUserId = userId
        viewModelScope.launch {
            performFetch(userId)
        }
    }

    fun addBirthday(userId: String, name: String, year: Int, month: Int, day: Int, remarks: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val calculatedAge = calculateAge(year, month, day)

            val newBirthday = Birthday(
                id = 0,
                userId = userId,
                name = name,
                birthYear = year,
                birthMonth = month,
                birthDayOfMonth = day,
                description = remarks,
                pictureUrl = null,
                age = calculatedAge
            )

            when (val result = repository.addBirthday(newBirthday)) {
                is NetworkResult.Success -> {
                    performFetch(userId)
                    _navigationEvent.emit(true)
                }
                is NetworkResult.Error -> {
                    _errorMessage.value = result.message
                }
                else -> {}
            }
            _isLoading.value = false
        }
    }

    fun deleteBirthday(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repository.deleteBirthday(id)) {
                is NetworkResult.Success -> {
                    currentUserId?.let { performFetch(it) }
                }
                is NetworkResult.Error -> {
                    _errorMessage.value = result.message
                }
                else -> {}
            }
            _isLoading.value = false
        }
    }

    fun getBirthdayById(id: Int): Birthday? {
        return _birthdays.value.find { it.id == id }
    }

    fun updateBirthday(id: Int, name: String, year: Int, month: Int, day: Int, remarks: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val existing = getBirthdayById(id)
            if (existing != null) {
                val updatedBirthday = existing.copy(
                    name = name,
                    birthYear = year,
                    birthMonth = month,
                    birthDayOfMonth = day,
                    description = remarks
                )
                when (val result = repository.updateBirthday(id, updatedBirthday)) {
                    is NetworkResult.Success -> {
                        currentUserId?.let { performFetch(it) }
                        _navigationEvent.emit(true)
                    }
                    is NetworkResult.Error -> {
                        _errorMessage.value = result.message
                    }
                    else -> {}
                }
            }
            _isLoading.value = false
        }
    }
}
