package com.example.mandatoryassignment_birthday.data.model

import com.google.gson.annotations.SerializedName

data class Birthday (
    @SerializedName("id")
    val id: Int,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("birthYear")
    val birthYear: Int,
    @SerializedName("birthMonth")
    val birthMonth: Int,
    @SerializedName("birthDayOfMonth")
    val birthDayOfMonth: Int,
    @SerializedName("remarks")
    val description: String,
    @SerializedName("pictureUrl")
    val pictureUrl: String,
    @SerializedName("age")
    val age: Int
)