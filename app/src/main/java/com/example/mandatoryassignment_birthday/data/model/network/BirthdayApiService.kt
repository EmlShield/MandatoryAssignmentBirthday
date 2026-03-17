package com.example.mandatoryassignment_birthday.data.model.network

import com.example.mandatoryassignment_birthday.data.model.Birthday
import retrofit2.http.GET

interface BirthdayApiService {
    @GET("persons")
    suspend fun getBirthdays(): List<Birthday>

    // TODO: Add POST, PUT, DELETE methods later
}