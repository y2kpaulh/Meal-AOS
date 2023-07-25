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

    override fun onCreate() {
        prefs = PreferenceUtil(applicationContext)
        context = applicationContext

        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
