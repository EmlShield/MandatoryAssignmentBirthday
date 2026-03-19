package com.example.mandatoryassignment_birthday.data.repository

import com.example.mandatoryassignment_birthday.data.model.Birthday
import com.example.mandatoryassignment_birthday.data.network.BirthdayApiService

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

    suspend fun addBirthday(birthday: Birthday): Boolean {
        return try {
            apiService.addBirthday(birthday)
            true // Return true if successful
        } catch (e: Exception) {
            println("API_DEBUG: Add failed: ${e.message}")
            false // Return false if it failed
        }
    }
}