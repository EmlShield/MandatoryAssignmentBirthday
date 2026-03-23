package com.example.mandatoryassignment_birthday.data.repository

import com.example.mandatoryassignment_birthday.data.model.Birthday
import com.example.mandatoryassignment_birthday.data.network.BirthdayApiService

class BirthdayRepository(private val apiService: BirthdayApiService) {
    suspend fun getBirthdays(userId: String): List<Birthday> {
        return try {
            val response = apiService.getBirthdays(userId)
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

    suspend fun deleteBirthday(id: Int): Boolean {
        return try {
            apiService.deleteBirthday(id)
            true
        } catch (e: Exception) {
            println("API_DEBUG: Delete failed: ${e.message}")
            false
        }
    }

    suspend fun updateBirthday(id: Int, birthday: Birthday): Boolean {
        return try {
            apiService.updateBirthday(id, birthday)
            true
        } catch (e: Exception) {
            println("API_DEBUG: Update failed: ${e.message}")
            false
        }
    }
}