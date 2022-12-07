package com.echadworks.meal.model

import android.content.Context
import android.util.Log
import java.io.IOException

public class Utils {
    fun getAssetJsonData(context: Context, fileName: String): String? {
        val json: String
        try {
            val inputStream = context.getAssets().open("$fileName.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.use { it.read(buffer) }
            json = String(buffer)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        // print the data
        //Log.i("data", json)
        return json
    }
}