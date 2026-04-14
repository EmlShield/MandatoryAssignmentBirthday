package com.example.mandatoryassignment_birthday.di

import com.example.mandatoryassignment_birthday.data.network.BirthdayApiService
import com.example.mandatoryassignment_birthday.data.repository.AuthRepository
import com.example.mandatoryassignment_birthday.data.repository.BirthdayRepository
import com.example.mandatoryassignment_birthday.viewmodel.AuthViewModel
import com.example.mandatoryassignment_birthday.viewmodel.BirthdayViewModel
import com.example.mandatoryassignment_birthday.BuildConfig
import com.example.mandatoryassignment_birthday.data.repository.ImageRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import okhttp3.OkHttpClient
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val appModule = module {
    single {
        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>().create(BirthdayApiService::class.java) }

    single { BirthdayRepository(get()) }

    viewModel { BirthdayViewModel(get(), get()) }

    single { FirebaseAuth.getInstance() }

    single { AuthRepository(get()) }

    viewModel { AuthViewModel(get()) }

    single { FirebaseStorage.getInstance() }

    single { ImageRepository(get()) }
}