package com.echadworks.meal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.echadworks.meal.model.Bible
import com.echadworks.meal.model.Plan
import com.echadworks.meal.model.Utils
import com.echadworks.meal.network.ApiProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import retrofit2.Call
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configBible()

        GlobalScope.launch(Dispatchers.IO) {
            async {
                getMealPlan()
            }
        }

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

    suspend fun getMealPlan() {
        ApiProvider.mealApi().getMealPlan().enqueue(object : retrofit2.Callback<List<Plan>> {
            override fun onResponse(
                call: Call<List<Plan>>,
                response: Response<List<Plan>>
            ) {
                Log.d(TAG, "success!!\n" + response.body()!!.toString())
            }

            override fun onFailure(
                call: Call<List<Plan>>,
                t: Throwable
            ) {
                Log.d(TAG, t.message.toString())
            }
        })
    }
}