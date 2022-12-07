package com.echadworks.meal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.echadworks.meal.model.Bible
import com.echadworks.meal.model.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configBible()
    }

    private fun configBible() {
        val utils = Utils()

        val jsonString = utils.getAssetJsonData(applicationContext, "NKRV")

        val gson = Gson()
        val bibleType = object : TypeToken<Bible>() {}.type
        val bible: Bible = gson.fromJson(jsonString, bibleType)

        val item: Bible.BibleItem = bible.get(0)

        Log.i("MainActivity", item.chapters[0][0])
    }
}