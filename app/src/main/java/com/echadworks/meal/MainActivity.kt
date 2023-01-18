package com.echadworks.meal

import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.echadworks.meal.databinding.ActivityMainBinding
import com.echadworks.meal.utils.Globals
import com.echadworks.meal.utils.Utils
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"

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
        binding.recyclerView.adapter = MealPlanAdapter(ArrayList())

        viewModel.todayVerse.observe(this, Observer{
            Log.d(TAG, ">>>>>>>>>>>")
            Log.d(TAG, it.toString())
            (binding.recyclerView.adapter as MealPlanAdapter).setData(it) //setData함수는 TodoAdapter에서 추가하겠습니다.
        })

        viewModel.todayDescription.observe(this, Observer{
            binding.tvInfo.text = it
        })

//        binding.button.setOnClickListener {
//            val bottomSheet = BottomSheetDialog(this)
//            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
//        }

    }

    override fun onResume() {
        super.onResume()
        requestPermissions()

        binding.tvDate.text = Globals.today()

        viewModel.getTodayPlan()
    }
}