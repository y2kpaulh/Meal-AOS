package com.echadworks.meal

import android.app.Application
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

enum class ActionType {
    PLUS, MINUS
}

class MainViewModel(application: Application): AndroidViewModel(application) {
    companion object {
        const val TAG: String = "MainViewModel"
    }

    private val context = getApplication<Application>().applicationContext

    lateinit var planList: List<Plan>
    lateinit var bible: Bible
    lateinit var todayPlan: Plan
    lateinit var todayBook: Bible.Book
    lateinit var planData: PlanData
    val todayDate = Globals.todayString()
    var todayDescription = MutableLiveData<String>()

    private val _todayVerse = MutableLiveData<ArrayList<Verse>>()
    val todayVerse: LiveData<ArrayList<Verse>>
        get() = _todayVerse

    lateinit var dataSource: ArrayList<Verse>

    fun configBible() {
        val bibleJsonString = Utils().getAssetJsonData(context,"NKRV")
        val bibleType = object : TypeToken<Bible>() {}.type

        bible = Gson().fromJson(bibleJsonString, bibleType)
        planList = listOf()
        todayPlan = Plan()
        planData = PlanData()
        dataSource = arrayListOf()
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
                    getPlanData()
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

    suspend fun getPlanData() {
        Log.d(TAG, "planList: " + planList)
        val plan = planList.filter {
            it.day == todayDate
        }
        todayPlan = plan[0]
        Log.d(TAG, "todayPlan: " + plan)

        val planBook = bible.filter {
            it.abbrev == todayPlan.book
        }

        todayBook = planBook[0]

        Log.d(TAG, todayBook.name)

        var verseList: List<String> = listOf()

        if (todayPlan.fChap == todayPlan.lChap) {
            val todayChapter = todayBook.chapters[todayPlan.fChap!! - 1]
            verseList = todayChapter.subList(todayPlan.fVer!!-1, todayPlan.lVer!!)
        } else {
            val firstChapter = todayBook.chapters[todayPlan.fChap!! - 1]
            val lastChapter = todayBook.chapters[todayPlan.lChap!! - 1]
            val todayVerse1 = firstChapter.subList(todayPlan.fVer!!-1, firstChapter.size)
            val todayVerse2 = lastChapter.subList(0, todayPlan.lVer!!)

            verseList = todayVerse1 + todayVerse2
        }

        verseList.forEachIndexed { index, string ->
            var verseNum: Int = 0

            if (todayPlan.fChap == todayPlan.lChap) {
                verseNum = index + todayPlan.fVer!!
            } else {
                 verseNum = index + todayPlan.fVer!!

                 var verseCount = todayBook.chapters[todayPlan.fChap!! - 1].size

                if (verseNum > verseCount) {
                    verseNum = verseNum - verseCount
                }
            }

            var verse = Verse(verseNum,string)

            dataSource.add(verse)
        }

        _todayVerse.value = dataSource

        todayDescription.value = String.format("%s\n%s %s:%s-%s:%s",Globals.today(), todayBook.name, todayPlan.fChap.toString(), todayPlan.fVer.toString(), todayPlan.lChap.toString(), todayPlan.lVer.toString())

        val planData = PlanData(todayBook.name, verseList)
        Log.d(TAG, planData.toString())
    }
}