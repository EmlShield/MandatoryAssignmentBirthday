package com.example.mandatoryassignment_birthday.data.network

import com.example.mandatoryassignment_birthday.data.model.Birthday
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface BirthdayApiService {
    @GET("persons")
    suspend fun getBirthdays(): List<Birthday>

    @POST("persons")
    suspend fun addBirthday(@Body birthday: Birthday): Birthday

    @DELETE("persons/{id}")
    suspend fun deleteBirthday(@Path("id") id: Int): Birthday

    @PUT("persons/{id}")
    suspend fun updateBirthday(@Path("id") id: Int, @Body birthday: Birthday): Birthday
}