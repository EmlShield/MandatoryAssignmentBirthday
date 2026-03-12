package com.example.mandatoryassignment_birthday.data.model.repository

import com.example.mandatoryassignment_birthday.data.model.Birthday
import com.example.mandatoryassignment_birthday.data.model.network.BirthdayApiService

class BirthdayRepository(private val apiService: BirthdayApiService) {
    suspend fun getBirthdays(): List<Birthday> {
        return try {
            apiService.getBirthdays()
        } catch (e: Exception) {
            // TODO: Handle errors
            emptyList()
        }
    }
}