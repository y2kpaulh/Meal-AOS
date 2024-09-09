package com.echadworks.meal.utils

import android.Manifest
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class Globals {
    companion object{
        val APP_SERVER_URL : String = "https://mealplan-y2kpaulh.koyeb.app"

        val requiredPermissions = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE
        )

        fun todayString(): String {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val current = LocalDateTime.now().format(formatter)
            println("today: $current")

            return current
        }

        fun convertStringToDate(dateString: String): LocalDate {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            return LocalDate.parse(dateString, formatter)
        }

        fun headerDateString(date: LocalDate): String {
            val formatter = DateTimeFormatter.ofPattern("MM/dd, E요일", Locale.KOREAN)
            val dateString = date.format(formatter)
            println("dateString: $dateString")

            return dateString
        }
    }
}