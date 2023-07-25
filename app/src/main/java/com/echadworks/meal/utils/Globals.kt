package com.echadworks.meal.utils

import android.Manifest
import com.echadworks.meal.MealApp.Companion.context
import com.echadworks.meal.model.Bible
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object Globals {
    const val APP_SERVER_URL : String = "https://mealplan-y2kpaulh.koyeb.app"

    val requiredPermissions = arrayOf(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE
    )

    private fun getBible(): Bible {
        val bibleJsonString = context?.let { Utils().getAssetJsonData(it, "NKRV") }
        val bibleType = object : TypeToken<Bible>() {}.type
        return Gson().fromJson(bibleJsonString, bibleType)
    }

    fun getBook(book: String): Bible.Book {
        val planBook = getBible().filter {
            it.abbrev == book
        }
        return planBook[0]
    }

    fun todayString(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ISO_DATE

        return current.format(formatter)
    }

    fun changeSelectedDate(day: String): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val date = formatter.parse(day)
        // 날짜, 시간을 가져오고 싶은 형태 선언
        val dateFormat = SimpleDateFormat("MM/dd, E요일", Locale("ko", "KR"))
        // 현재 시간을 dateFormat 에 선언한 형태의 String 으로 변환
        return dateFormat.format(date)
    }
}