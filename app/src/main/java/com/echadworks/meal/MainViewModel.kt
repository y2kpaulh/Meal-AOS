package com.echadworks.meal

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.util.Log
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

class MainViewModel(application: Application): AndroidViewModel(application) {
    companion object {
        const val TAG: String = "MainViewModel"
    }

    private val context = application.applicationContext

    lateinit var planList: List<Plan>
    private lateinit var bible: Bible
    private lateinit var todayPlan: Plan
    private lateinit var todayBook: Bible.Book
    private lateinit var planData: PlanData
    private val todayDate = Globals.todayString()
    var todayDescription = MutableLiveData<String>()

    private val _todayVerse = MutableLiveData<ArrayList<Verse>>()
    val todayVerse: LiveData<ArrayList<Verse>>
        get() = _todayVerse

    private var _scheduleList = MutableLiveData<List<Plan>>()
    val scheduleList: MutableLiveData<List<Plan>> get() = _scheduleList

    private lateinit var dataSource: ArrayList<Verse>

    private val gson = com.google.gson.Gson()

    private var _scheduleDate = MutableLiveData<String>()
    val scheduleDate: MutableLiveData<String> get() = _scheduleDate

    fun configBible() {
        val bibleJsonString = Utils().getAssetJsonData(context,"NKRV")
        val bibleType = object : TypeToken<Bible>() {}.type

        bible = Gson().fromJson(bibleJsonString, bibleType)
        planList = listOf()
        todayPlan = Plan()
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

    fun getTodayPlan() {
         val checkedPlan: Plan? = existTodayPlan()

        if (checkedPlan != null) {
            todayPlan = checkedPlan
             Log.d("exist todayPlan: %s", todayPlan.toString())

            updateTodayPlan()
        } else {
            runBlocking {
                getMealPlan()
            }
        }
    }

    fun setDatePlan(plan: Plan) {
        todayPlan = plan
        Log.d("exist todayPlan: %s", todayPlan.toString())

        updateTodayPlan()
    }

    private fun existTodayPlan() : Plan? {
        val mealPlan = readSavedMealPlan() ?: listOf()
        val todayIndex = getTodayIndex(mealPlan)

        if (mealPlan.isNotEmpty() && todayIndex != null) {
            _scheduleList.value = mealPlan

            Log.d("", "todayIndex: $todayIndex")
            return mealPlan[todayIndex]
        }

        return null
    }

    private suspend fun getMealPlan() {
        ApiProvider.mealApi().getMealPlan().enqueue(object : retrofit2.Callback<List<Plan>> {
            override fun onResponse(
                call: Call<List<Plan>>,
                response: Response<List<Plan>>
            ) {
                planList = response.body()!!

                _scheduleList.value = planList

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

        val today = Globals.todayString()
        val plan = planList.filter {
            it.day == today
        }
        todayPlan = plan[0]
        Log.d(TAG, "downloaded todayPlan: $plan")

        updateTodayPlan()
    }

    private fun generateNumericArray(start: Int, end: Int, step: Int): IntArray {
        val size = ((end - start) / step) + 1
        return IntArray(size) { start + it * step }
    }

    private fun updateTodayPlan() {
        val planBook = bible.filter {
            it.abbrev ==  todayPlan.book
        }

        todayBook = planBook[0]

        Log.d(TAG, todayBook.name)

        var verseList: MutableList<String> = mutableListOf()
        val verseNumList: MutableList<Int> = mutableListOf()

        val startChapterIndex = todayPlan.fChap!!
        val startVerse = todayPlan.fVer!!

        val endChapterIndex = todayPlan.lChap!!
        val endVerse = todayPlan.lVer!!

        if (todayPlan.fChap == todayPlan.lChap) {
            val todayChapter = todayBook.chapters[todayPlan.fChap!! - 1]
            verseList = todayChapter.subList(todayPlan.fVer!! - 1, todayPlan.lVer!!).toMutableList()

            val chapter = todayBook.chapters[startChapterIndex-1]
            var sliceStartIndex = startVerse-1
            var sliceEndIndex = endVerse

            val verseText = chapter.subList(sliceStartIndex, sliceEndIndex)
            println(verseText)

            val numericArray = generateNumericArray(sliceStartIndex+1, sliceEndIndex, 1)
            println(numericArray)

        } else {
            for (chapterIndex: Int in startChapterIndex-1 until endChapterIndex) {
                val chapter = todayBook.chapters[chapterIndex]
                var sliceStartIndex = 0
                var sliceEndIndex = 0

                if (chapterIndex == startChapterIndex-1) {
                    sliceStartIndex = startVerse-1
                    sliceEndIndex = chapter.size
                } else if (chapterIndex == endChapterIndex) {
                    sliceStartIndex = 0
                    sliceEndIndex = endVerse + 1
                } else {
                    sliceStartIndex = 0
                    sliceEndIndex = chapter.size
                }

                val verseText = chapter.subList(sliceStartIndex, sliceEndIndex)
                println(verseText)
                verseList.addAll(verseText)

                val numericArray = generateNumericArray(sliceStartIndex+1, sliceEndIndex, 1)
                println(numericArray)
                verseNumList.addAll(numericArray.toList())
            }
        }

        dataSource = arrayListOf()

        verseList.forEachIndexed { index, string ->
            var verseNum: Int = 0

            if (todayPlan.fChap == todayPlan.lChap) {
                verseNum = index + todayPlan.fVer!!
                dataSource.add(Verse(verseNum, string))
            } else {
                dataSource.add(Verse(verseNumList[index], string))
            }
        }

        _todayVerse.value = dataSource

        todayDescription.value = String.format(
            "%s %s:%s-%s:%s",
            todayBook.name,
            todayPlan.fChap.toString(),
            todayPlan.fVer.toString(),
            todayPlan.lChap.toString(),
            todayPlan.lVer.toString()
        )

        todayPlan.day?.let { dateStr ->
          _scheduleDate.value = Globals.convertStringToDate(dateStr).let { localDate ->
              Globals.headerDateString(localDate)
           }
        }
    }

    fun getTodayIndex(planList: List<Plan>): Int? {
        val todayString = Globals.todayString()
        return findIndexUsingMapIndexedNotNull(planList, todayString)
    }

    private fun findIndexUsingMapIndexedNotNull(list: List<Plan>, day: String): Int? {
        return list.mapIndexedNotNull { index, plan ->
            if (plan.day == day) index else null
        }.firstOrNull()
    }
}