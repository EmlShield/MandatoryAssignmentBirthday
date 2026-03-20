package com.example.mandatoryassignment_birthday.data.network

import com.example.mandatoryassignment_birthday.data.model.Birthday
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BirthdayApiService {
    @GET("persons")
    suspend fun getBirthdays(): List<Birthday>

    // @Body tells Retrofit to convert the birthday object to JSON
    @POST("persons")
    suspend fun addBirthday(@Body birthday: Birthday): Birthday

    @DELETE("persons/{id}")
    suspend fun deleteBirthday(@Path("id") id: Int): Birthday

    // TODO: Add PUT methods later
}