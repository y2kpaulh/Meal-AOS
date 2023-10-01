package com.echadworks.meal

import android.app.Application
import android.content.Context
import com.echadworks.meal.utils.PreferenceUtil
import com.echadworks.meal.utils.ThemeHelper
import com.echadworks.meal.utils.ThemeHelper.applyTheme
import timber.log.Timber

class MealApp : Application() {
    companion object {
        var context: Context? = null
        lateinit var prefs: PreferenceUtil
    }

    private val context: Context? = null

    override fun onCreate() {
        prefs = PreferenceUtil(applicationContext)
        MealApp.context = getApplicationContext()
        val themePref = prefs.getString("themePref", ThemeHelper.DEFAULT_MODE)
        applyTheme(themePref ?: ThemeHelper.DEFAULT_MODE)

        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
