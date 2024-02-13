package com.echadworks.meal.network

import com.echadworks.meal.utils.Globals
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiProvider {
    private var mealApi: MealApi? = null

    fun mealApi(): MealApi {
        if (mealApi == null) {
            mealApi = Retrofit.Builder()
                .baseUrl(Globals.APP_SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create(Gson()))
                .client(ApiService.getOkHttpClient())
                .build()
                .create(MealApi::class.java)
        }
        return mealApi!!
    }
}