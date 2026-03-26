package com.example.mandatoryassignment_birthday.data.model

import java.time.LocalDate
import java.time.Period

data class User (
    val userId: String,
    val email: String?
    // TODO: Add display name or profile picture URL if needed later

)

/*
fun calculateAge(year: Int, month: Int, day: Int): Int {
    val birthDate = LocalDate.of(year, month, day)
    val today = LocalDate.now()
    return Period.between(birthDate, today).years
}
*/