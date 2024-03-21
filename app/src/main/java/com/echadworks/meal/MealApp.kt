package com.echadworks.meal

import android.app.Application
import android.content.Context
import com.echadworks.meal.utils.PreferenceUtil

class MealApp : Application() {
    companion object {
        lateinit var prefs: PreferenceUtil
    }

    override fun onCreate() {
        prefs = PreferenceUtil(applicationContext)
        super.onCreate()
    }
}
