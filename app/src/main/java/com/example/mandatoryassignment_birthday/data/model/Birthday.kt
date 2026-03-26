package com.example.mandatoryassignment_birthday.data.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit

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
    val description: String?,
    @SerializedName("pictureUrl")
    val pictureUrl: String?,
    @SerializedName("age")
    val age: Int? = null
) {
    fun daysUntilNextBirthday(): Long {
        val today = LocalDate.now()

        var nextBirthday = LocalDate.of(today.year, birthMonth, birthDayOfMonth)

        if (nextBirthday.isBefore(today) || nextBirthday.isEqual(today)) {
            nextBirthday = nextBirthday.plusYears(1)
        }
        return ChronoUnit.DAYS.between(today, nextBirthday)
    }
}

fun calculateAge(year: Int, month: Int, day: Int): Int {
    val birthDate = LocalDate.of(year, month, day)
    val today = LocalDate.now()
    return Period.between(birthDate, today).years
}