package com.echadworks.meal.utils

import android.Manifest
import java.text.SimpleDateFormat
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
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ISO_DATE
            val formatted = current.format(formatter)

            return formatted
        }

        fun today() : String{
            // 현재시간을 가져오기
            val long_now = System.currentTimeMillis()
            // 현재 시간을 Date 타입으로 변환
            val t_date = Date(long_now)
            // 날짜, 시간을 가져오고 싶은 형태 선언
            val t_dateFormat = SimpleDateFormat("MM/dd, E요일", Locale("ko", "KR"))
            // 현재 시간을 dateFormat 에 선언한 형태의 String 으로 변환
            val str_date = t_dateFormat.format(t_date)

            return str_date
        }

        fun convertStringToDate(dateString: String): Date? {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return try {
                format.parse(dateString)
            } catch (e: Exception) {
                // 변환이 실패하면 null 반환
                null
            }
        }

        fun dateString(date: Date) : String{
            // 날짜, 시간을 가져오고 싶은 형태 선언
            val t_dateFormat = SimpleDateFormat("MM/dd, E요일", Locale("ko", "KR"))
            // 현재 시간을 dateFormat 에 선언한 형태의 String 으로 변환
            val str_date = t_dateFormat.format(date)

            return str_date
        }
    }
}