package com.echadworks.meal.view.main

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.echadworks.meal.model.Bible
import com.echadworks.meal.model.PlanData
import com.echadworks.meal.model.Verse
import com.echadworks.meal.network.ApiProvider
import com.echadworks.meal.network.Plan
import com.echadworks.meal.utils.Globals
import com.echadworks.meal.utils.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainViewModel(application: Application): AndroidViewModel(application) {
    companion object {
        const val TAG: String = "MainViewModel"
    }

    private val context = getApplication<Application>().applicationContext

    var planList : List<Plan> = arrayListOf()
    lateinit var selectedDayPlan: Plan
    lateinit var planData: PlanData

    var selectedDayDescription = MutableLiveData<String>()

    private val _selectedDayVerse = MutableLiveData<ArrayList<Verse>>()
    val selectedDayVerse: LiveData<ArrayList<Verse>>
        get() = _selectedDayVerse

    lateinit var dataSource: ArrayList<Verse>

    private val gson = com.google.gson.Gson()

    fun configBible() {
        selectedDayPlan = Plan()
        planData = PlanData()
        dataSource = arrayListOf()
    }

    private fun readSavedMealPlan() : List<Plan>? {
        // creating a new variable for gson.
        val sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE)

        val mealPlanStr = sharedPreferences.getString("mealPlan", "") ?: ""

        if (mealPlanStr.isNotEmpty()) {
            return gson.fromJson(mealPlanStr, Array<Plan>::class.java).toList()
        }

        return  null
    }

    fun fetchReadingPlan() {
        val date: String = if (selectedDayPlan.day != null) selectedDayPlan.day!! else Globals.todayString()
        val resultPlan = existSelectedDayPlan(date)

        if (resultPlan != null) {
            selectedDayPlan = resultPlan
            updateSelectedDayPlan(selectedDayPlan)
        }else {
            runBlocking {
                getMealPlan()
            }
        }
    }

    private fun existTodayPlan() : Plan? {
        val mealPlan = readSavedMealPlan() ?: listOf()

        if (mealPlan.isNotEmpty()) {
            planList = mealPlan
            val plan = mealPlan.find { plan: Plan ->
                plan.day.equals(Globals.todayString())
            }
            return plan
        }

        return null
    }

    private fun existSelectedDayPlan(selectedDay: String) : Plan? {
        val mealPlan = readSavedMealPlan() ?: listOf()

        if (mealPlan.isNotEmpty()) {
            planList = mealPlan
            val plan = mealPlan.find { plan: Plan ->
                plan.day.equals(selectedDay)
            }
            return plan
        }

        return null
    }

    private suspend fun getMealPlan() {
        ApiProvider.mealApi().getMealPlan().enqueue(object : retrofit2.Callback<List<Plan>> {
            override fun onResponse(
                call: Call<List<Plan>>,
                response: Response<List<Plan>>
            ) {
                Log.d(TAG, "success!!\n" + response.body()!!.toString())
                planList = response.body()!!
                saveMealPlan()

                getPlanData()
            }

            override fun onFailure(
                call: Call<List<Plan>>,
                t: Throwable
            ) {
                Log.d(TAG, t.message.toString())
            }
        })
    }

    private fun saveMealPlan() {
        // method for saving the data in array list.
        // creating a variable for storing data in
        // shared preferences.
        val sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE)

        // creating a variable for editor to
        // store data in shared preferences.
        val editor = sharedPreferences.edit()

        // getting data from gson and storing it in a string.
        val json: String = gson.toJson(planList)

        // below line is to save data in shared
        // prefs in the form of string.
        editor.putString("mealPlan", json)

        // below line is to apply changes
        // and save data in shared prefs.
        editor.apply()
    }

    private fun getPlanData() {
        Log.d(TAG, "planList: " + planList)

        val plan = planList.filter {
            it.day == Globals.todayString()
        }

        selectedDayPlan = plan?.get(0).let { it } ?: return

        updateSelectedDayPlan(selectedDayPlan)
    }

    private fun updateSelectedDayPlan(selectedPlan: Plan) {
        val book = selectedPlan.book?.let { Globals.getBook(it) } ?: return
        val plan: Plan = selectedPlan.let { it }
        val fChap: Int = plan.fChap?.let { it } ?: return
        val fVer: Int = plan.fVer?.let { it } ?: return
        val lChap: Int = plan.lChap?.let { it } ?: return
        val lVer: Int = plan.lVer?.let { it } ?: return

        var verseList: List<String> = if (fChap == lChap) {
            val todayChapter = book.chapters[fChap - 1]
            todayChapter.subList(fVer - 1, lVer)
        } else {
            val firstChapter = book.chapters[fChap - 1]
            val lastChapter = book.chapters[lChap - 1]
            val todayVerse1 = firstChapter.subList(fVer - 1, firstChapter.size)
            val todayVerse2 = lastChapter.subList(0, lVer)

            todayVerse1 + todayVerse2
        }

        dataSource = arrayListOf()

        verseList.forEachIndexed { index, string ->
            var verseNum: Int

            if (fChap == lChap) {
                verseNum = index + fVer
            } else {
                verseNum = index + fVer

                var verseCount = book.chapters[fChap - 1].size

                if (verseNum > verseCount) {
                    verseNum -= verseCount
                }
            }

            dataSource.add(Verse(verseNum, string))
        }

        _selectedDayVerse.value = dataSource

        selectedDayDescription.value = String.format(
            "%s %s:%s-%s:%s",
            book.name,
            fChap.toString(),
            fVer.toString(),
            lChap.toString(),
            lVer.toString()
        )

        val planData = PlanData(book.name, verseList)
        Log.d(TAG, planData.toString())
    }

    fun getTodayIndex(): Int {
        var result: Int = 0

        if (planList.size > 0) {
            result = planList.withIndex().let {
                it?.let { plan ->
                    plan.first { data -> Globals.todayString() == data.value.day}.let {
                            index -> index
                    }.index ?: 0
                }
            } ?: 0
        }

        return result
    }


}