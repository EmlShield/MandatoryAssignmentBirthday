package com.example.mandatoryassignment_birthday.di

import com.example.mandatoryassignment_birthday.data.model.network.BirthdayApiService
import com.example.mandatoryassignment_birthday.data.model.repository.BirthdayRepository
import com.example.mandatoryassignment_birthday.viewmodel.BirthdayViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    // 1. Provide Retrofit
    single {
        Retrofit.Builder()
            .baseUrl("https://birthdaysrest.azurewebsites.net/api/persons")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 2. Provide API Service
    single { get<Retrofit>().create(BirthdayApiService::class.java) }

    // 3. Provide the Repository
    single { BirthdayRepository(get()) }

    // 4. Provide the ViewModel
    viewModel { BirthdayViewModel(get()) }
}