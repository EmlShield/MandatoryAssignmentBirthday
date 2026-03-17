package com.example.mandatoryassignment_birthday.data.model.repository

import com.example.mandatoryassignment_birthday.data.model.Birthday
import com.example.mandatoryassignment_birthday.data.model.network.BirthdayApiService

class BirthdayRepository(private val apiService: BirthdayApiService) {
    suspend fun getBirthdays(): List<Birthday> {
        return try {
            val response = apiService.getBirthdays()
            println("API_DEBUG: Data received: ${response.size} items")
            response
        } catch (e: Exception) {
            println("API_DEBUG: Error fetching data: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }
}