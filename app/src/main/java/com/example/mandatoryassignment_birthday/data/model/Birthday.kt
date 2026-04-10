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
    val age: Int
) {
    fun daysUntilNextBirthday(): Long {
        val today = LocalDate.now()
        val validMonth = birthMonth.coerceIn(1, 12)
        val validDay = birthDayOfMonth.coerceIn(1, 31)

        var nextBirthday = try {
            LocalDate.of(today.year, validMonth, validDay)
        } catch (e: Exception) {
            LocalDate.of(today.year, validMonth, 1).withDayOfMonth(1).plusMonths(1).minusDays(1)
        }

        if (nextBirthday.isBefore(today) || nextBirthday.isEqual(today)) {
            nextBirthday = nextBirthday.plusYears(1)
        }
        return ChronoUnit.DAYS.between(today, nextBirthday)
    }

    val displayAge: Int
        get() {
            return try {
                val birthDate = LocalDate.of(birthYear, birthMonth.coerceIn(1, 12), birthDayOfMonth.coerceIn(1, 31))
                val today = LocalDate.now()
                val calculated = Period.between(birthDate, today).years
                if (calculated < 0) 0 else calculated
            } catch (e: Exception) {
                0
            }
        }
}
