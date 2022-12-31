package com.echadworks.meal

import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.echadworks.meal.model.Bible
import com.echadworks.meal.model.PlanData
import com.echadworks.meal.network.ApiProvider
import com.echadworks.meal.network.Plan
import com.echadworks.meal.utils.Globals
import com.echadworks.meal.utils.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"

    lateinit var planList: List<Plan>
    lateinit var bible: Bible
    lateinit var todayPlan: Plan
    lateinit var planData: PlanData
    val todayDate = Globals.todayString()

    val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions.containsValue(false)) {
            requestPermissions()
        }
    }
    private fun requestPermissions() {
        permissionLauncher.launch(Globals.requiredPermissions)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configBible()

        GlobalScope.launch(Dispatchers.IO) {
            runBlocking {
                getMealPlan()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requestPermissions()
    }

    private fun configBible() {
        val utils = Utils()
        val bibleJsonString = utils.getAssetJsonData(applicationContext, "NKRV")
        val bibleType = object : TypeToken<Bible>() {}.type
        bible = Gson().fromJson(bibleJsonString, bibleType)
        val book: Bible.Book = bible.get(0)
        
        planList = listOf()
        todayPlan = Plan()
        planData = PlanData()
    }

    suspend fun getMealPlan() {
        ApiProvider.mealApi().getMealPlan().enqueue(object : retrofit2.Callback<List<Plan>> {
            override fun onResponse(
                call: Call<List<Plan>>,
                response: Response<List<Plan>>
            ) {
                Log.d(TAG, "success!!\n" + response.body()!!.toString())
                planList = response.body()!!
               runBlocking {
                   planData()
               }
            }

            override fun onFailure(
                call: Call<List<Plan>>,
                t: Throwable
            ) {
                Log.d(TAG, t.message.toString())
            }
        })
    }

   suspend fun planData() {
        Log.d(TAG, "planList: " + planList)
        val plan = planList.filter {
            it.day == todayDate
        }
        todayPlan = plan[0]
        Log.d(TAG, "todayPlan: " + plan)

        val planBook = bible.filter {
            it.abbrev == todayPlan.book
        }

        val todayBook = planBook[0]

        Log.d(TAG, todayBook.name)

        var todayVerse: List<String> = listOf()

        if (todayPlan.fChap == todayPlan.lChap) {
            val todayChapter = todayBook.chapters[todayPlan.fChap!! - 1]
            todayVerse = todayChapter.subList(todayPlan.fVer!!-1, todayPlan.lVer!!)
        } else {
            val firstChapter = todayBook.chapters[todayPlan.fChap!! - 1]
            val lastChapter = todayBook.chapters[todayPlan.lChap!! - 1]
            val todayVerse1 = firstChapter.subList(todayPlan.fVer!!-1, firstChapter.size)
            val todayVerse2 = lastChapter.subList(0, todayPlan.lVer!!)

            todayVerse = todayVerse1 + todayVerse2
        }

        val planData = PlanData(todayBook.name, todayVerse)
        Log.d(TAG, String.format("%s %s %s:%s - %s:%s",Globals.today(), planData.name, todayPlan.fChap.toString(), todayPlan.fVer.toString(), todayPlan.lChap.toString(), todayPlan.lVer.toString()))
        Log.d(TAG, planData.toString())
    }
}