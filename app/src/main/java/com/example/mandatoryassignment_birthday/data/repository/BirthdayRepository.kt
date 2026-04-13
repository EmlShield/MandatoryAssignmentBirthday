package com.example.mandatoryassignment_birthday.data.repository

import android.util.Log
import com.example.mandatoryassignment_birthday.data.model.Birthday
import com.example.mandatoryassignment_birthday.data.network.BirthdayApiService
import com.example.mandatoryassignment_birthday.data.network.NetworkResult
import retrofit2.HttpException
import java.io.IOException

class BirthdayRepository(private val apiService: BirthdayApiService) {

    suspend fun getBirthdays(userId: String): NetworkResult<List<Birthday>> {
        return try {
            val response = apiService.getBirthdays()
            val filteredResponse = response.filter { it.userId == userId }
            NetworkResult.Success(filteredResponse)
        } catch (e: IOException) {
            Log.e("BirthdayRepository", "Network error fetching birthdays", e)
            NetworkResult.Error("No internet connection. Please check your network settings.", e)
        } catch (e: HttpException) {
            Log.e("BirthdayRepository", "HTTP error fetching birthdays: ${e.code()}", e)
            NetworkResult.Error("Server error (${e.code()}). Please try again later.", e)
        } catch (e: Exception) {
            Log.e("BirthdayRepository", "Unexpected error fetching birthdays", e)
            NetworkResult.Error("An unexpected error occurred: ${e.localizedMessage}", e)
        }
    }

    suspend fun addBirthday(birthday: Birthday): NetworkResult<Birthday> {
        return try {
            val result = apiService.addBirthday(birthday)
            NetworkResult.Success(result)
        } catch (e: Exception) {
            Log.e("BirthdayRepository", "Error adding birthday", e)
            NetworkResult.Error("Failed to add birthday. ${e.localizedMessage}", e)
        }
    }

    suspend fun deleteBirthday(id: Int): NetworkResult<Birthday> {
        return try {
            val result = apiService.deleteBirthday(id)
            NetworkResult.Success(result)
        } catch (e: Exception) {
            Log.e("BirthdayRepository", "Error deleting birthday with id: $id", e)
            NetworkResult.Error("Failed to delete. ${e.localizedMessage}", e)
        }
    }

    suspend fun updateBirthday(id: Int, birthday: Birthday): NetworkResult<Birthday> {
        return try {
            val result = apiService.updateBirthday(id, birthday)
            NetworkResult.Success(result)
        } catch (e: Exception) {
            Log.e("BirthdayRepository", "Error updating birthday with id: $id", e)
            NetworkResult.Error("Update failed. ${e.localizedMessage}", e)
        }
    }
}