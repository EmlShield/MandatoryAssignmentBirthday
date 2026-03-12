package com.example.mandatoryassignment_birthday.data.model

data class Birthday (
    val id: String,
    val userId: String,
    val name: String,
    val birthYear: Int,
    val birthMonth: Int,
    val birthDayOfMonth: Int,
    val remarks: String,
    val pictureUrl: String,
    val age: Int
)