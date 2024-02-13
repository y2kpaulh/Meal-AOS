package com.echadworks.meal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.echadworks.meal.databinding.ActivityMainBinding
import com.echadworks.meal.databinding.BottomSheetDialogScheduleBinding
import com.echadworks.meal.utils.Globals
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var bottomSheetDialog: BottomSheetDialog

    private var _scheduleBottomSheetDialogBinding: BottomSheetDialogScheduleBinding? = null
    private val scheduleBottomSheetDialogBinding get() = _scheduleBottomSheetDialogBinding

    private lateinit var viewModel: MainViewModel

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
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

        viewModel.getTodayPlan()
    }

    private fun config() {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.configBible()

        initObserver()
        initView()
    }

    private fun initObserver() {
        viewModel.todayVerse.observe(this) {
            (binding.recyclerView.adapter as MealPlanAdapter).setData(it)
        }

        viewModel.todayDescription.observe(this) {
            binding.tvInfo.text = it
            binding.tvDate.text = viewModel.todayBook.
        }

        viewModel.scheduleList.observe(this) {
            Log.d("", it.toString())
        }
    }

    private fun initView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = MealPlanAdapter(this) {
            Log.d("MainActivity","verse index: %s"+ it.toString())
        }

        _scheduleBottomSheetDialogBinding = BottomSheetDialogScheduleBinding.inflate(LayoutInflater.from(this),binding.root,false)
        bottomSheetDialog = BottomSheetDialog(this, R.style.bottom_sheet_dialog)

        binding.button.setOnClickListener {
            scheduleBottomSheetDialogBinding?.let { binding ->
                binding.tvTitle.text = "끼니 일정 리스트"

                binding.rvSchedule.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

                val adapter = ScheduleListAdapter(this) {
                    Log.d("MainActivity","verse index: %s"+ it.toString())

                    viewModel.scheduleList.value?.getOrNull(it)?.let { plan ->
                        Log.d("", plan.day ?: "")

                        val planDate = plan.day.orEmpty()
                        val date = Globals.convertStringToDate(planDate)

                        date?.let { dateStr ->
                            updateDateString(Globals.dateString(dateStr))
                        }

                        viewModel.setDatePlan(plan)
                    }

                    bottomSheetDialog.dismiss()
                }

                adapter.submitList(viewModel.scheduleList.value.orEmpty())

                binding.rvSchedule.adapter = adapter

                binding.rvSchedule.post {
                    binding.rvSchedule.scrollToPosition(30)
                }
                bottomSheetDialog.setContentView(binding.root)
                bottomSheetDialog.show()
            }
        }
    }

    fun updateDateString(date: String) {
        binding.tvDate.text = date
    }

    override fun onResume() {
        super.onResume()
        requestPermissions()

//        if (!binding.tvDate.text.equals(Globals.today())) {
//            binding.tvDate.text = Globals.today()
//            viewModel.getTodayPlan()
//        }
    }
}