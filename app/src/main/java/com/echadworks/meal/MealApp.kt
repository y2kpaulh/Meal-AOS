package com.echadworks.meal

import android.app.Application
import android.content.Context
import com.echadworks.meal.utils.PreferenceUtil
import timber.log.Timber

class MealApp : Application() {
    companion object {
        var context: Context? = null
        lateinit var prefs: PreferenceUtil
    }

    private val context: Context? = null

    override fun onCreate() {
        prefs = PreferenceUtil(applicationContext)
        MealApp.context = applicationContext

        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
