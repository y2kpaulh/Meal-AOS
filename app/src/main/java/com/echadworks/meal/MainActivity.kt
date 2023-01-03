package com.echadworks.meal

import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.echadworks.meal.databinding.ActivityMainBinding
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

        GlobalScope.launch(Dispatchers.IO) {
            runBlocking {
               viewModel.getMealPlan()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requestPermissions()
    }


}