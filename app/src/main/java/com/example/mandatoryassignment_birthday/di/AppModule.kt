package com.example.mandatoryassignment_birthday.di

import com.example.mandatoryassignment_birthday.data.network.BirthdayApiService
import com.example.mandatoryassignment_birthday.data.repository.AuthRepository
import com.example.mandatoryassignment_birthday.data.repository.BirthdayRepository
import com.example.mandatoryassignment_birthday.viewmodel.AuthViewModel
import com.example.mandatoryassignment_birthday.viewmodel.BirthdayViewModel
import com.example.mandatoryassignment_birthday.BuildConfig
import com.google.firebase.auth.FirebaseAuth
import okhttp3.OkHttpClient
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val appModule = module {
    // Provide Retrofit
    single {
        // Client with timeouts
        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client) // Use the client
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Provide API Service
    single { get<Retrofit>().create(BirthdayApiService::class.java) }

    // Provide the Repository
    single { BirthdayRepository(get()) }

    // Provide the ViewModel
    viewModel { BirthdayViewModel(get()) }

    // Provide FirebaseAuth instance
    single { FirebaseAuth.getInstance() }

    // Provide AuthRepository
    single { AuthRepository(get()) }

    // Provide AuthViewModel
    viewModel { AuthViewModel(get()) }
}