package com.example.mandatoryassignment_birthday.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.getValue

object RetrofitClient {
    private const val BASE_URL = "https://birthdaysrest.azurewebsites.net/api/"

    val instance: BirthdayApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(BirthdayApiService::class.java)
    }
}