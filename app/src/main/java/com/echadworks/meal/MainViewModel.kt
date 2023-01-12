package com.echadworks.meal

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.echadworks.meal.model.Bible
import com.echadworks.meal.model.PlanData
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
    lateinit var planData: PlanData
    val todayDate = Globals.todayString()
    private var todayVerseList = MutableLiveData<List<String>>()
    val todayVerse: LiveData<List<String>>
             get() = todayVerseList
    private val _currentValue = MutableLiveData<Int>()

    // 변경되지 않는 데이터를 가져올 때 이름을 _언더스코어 없이 설정
    // 공개적으로 가져오는 변수는 private 이 아닌 public으로 외부에서도 접근 가능하도록 설정
    // 하지만 값을 직접 라이브데이터에 접근하지 않고 뷰모델을 통해 가져올 수 있도록 설정
    val currentValue: LiveData<Int>
        get() = _currentValue

    // 초기값 설정
    init {
        Log.d(TAG, " MainViewModel - 생성자 호출")
        _currentValue.value = 0
    }

    fun updateValue(actionType: ActionType, input: Int){
        when(actionType){
            ActionType.PLUS ->
                _currentValue.value = _currentValue.value?.plus(input)
            ActionType.MINUS ->
                _currentValue.value = _currentValue.value?.minus(input)
        }
    }

    fun configBible() {
        val bibleJsonString = Utils().getAssetJsonData(context,"NKRV")
        val bibleType = object : TypeToken<Bible>() {}.type

        bible = Gson().fromJson(bibleJsonString, bibleType)
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

        val todayBook = planBook[0]

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

        todayVerseList.value = verseList

        val planData = PlanData(todayBook.name, verseList)
        Log.d(TAG, String.format("%s %s %s:%s - %s:%s",Globals.today(), planData.name, todayPlan.fChap.toString(), todayPlan.fVer.toString(), todayPlan.lChap.toString(), todayPlan.lVer.toString()))
        Log.d(TAG, planData.toString())
    }
}