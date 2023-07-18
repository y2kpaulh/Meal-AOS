package com.echadworks.meal.view.main

import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.echadworks.meal.databinding.ActivityMainBinding
import com.echadworks.meal.utils.Globals
import com.echadworks.meal.view.list.PlanAdapter
import com.echadworks.meal.view.list.ReadingPlanSheet

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    private lateinit var readingPlanSheet: ReadingPlanSheet
    lateinit var planAdapter: PlanAdapter
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    lateinit var viewModel: MainViewModel

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
        setContentView(binding.root)

        config()
    }

    private fun config() {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        viewModel.configBible()

        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = MealPlanAdapter()

        viewModel.planList.observe(this, Observer {
            readingPlanSheet.setPlanList(it)
        })

        viewModel.selectedDayVerse.observe(this, Observer{
            Log.d(TAG, ">>>>>>>>>>>")
            Log.d(TAG, it.toString())
            val selectedDay = viewModel.changeSelectedDate(viewModel.selectedDayPlan.day!!)
            binding.tvDate.text = selectedDay

            (binding.recyclerView.adapter as MealPlanAdapter).submitList(it) //setData함수는 TodoAdapter에서 추가하겠습니다.
        })

        viewModel.todayDescription.observe(this, Observer{
            binding.tvInfo.text = it
        })

        planAdapter = PlanAdapter { plan, position ->
            Log.d(TAG,"plan: $plan, position: $position")
            viewModel.selectedDayPlan = plan

            viewModel.fetchReadingPlan()

            readingPlanSheet.dismiss()
        }

        readingPlanSheet = ReadingPlanSheet()
        readingPlanSheet.setAdapter(planAdapter)

        binding.btnCalendar.setOnClickListener {
            readingPlanSheet.show(supportFragmentManager, "TAG")
        }
    }

    override fun onResume() {
        super.onResume()
        requestPermissions()

        viewModel.fetchReadingPlan()
    }
}