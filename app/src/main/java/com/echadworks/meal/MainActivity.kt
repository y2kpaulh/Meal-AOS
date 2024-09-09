package com.echadworks.meal

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.echadworks.meal.databinding.ActivityMainBinding
import com.echadworks.meal.databinding.BottomSheetAppInfoBinding
import com.echadworks.meal.databinding.BottomSheetScheduleBinding
import com.echadworks.meal.utils.Globals
import com.google.android.material.bottomsheet.BottomSheetDialog


class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var bottomSheetDialog: BottomSheetDialog

    private var _scheduleBottomSheetBinding: BottomSheetScheduleBinding? = null
    private var _appInfoBottomSheetDialogBinding: BottomSheetAppInfoBinding? = null

    private val scheduleBottomSheetBinding get() = _scheduleBottomSheetBinding
    private val appInfoBottomSheetDialogBinding get() = _appInfoBottomSheetDialogBinding

    private lateinit var viewModel: MainViewModel
    private lateinit var checkBoxMenu: ArrayList<CheckBox>

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions.containsValue(false)) {
            requestPermissions()
        }
    }
    private fun requestPermissions() {
        permissionLauncher.launch(Globals.requiredPermissions)
    }
    private lateinit var sharedPreferences: SharedPreferences
    private var themeIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        config()

        viewModel.getTodayPlan()
    }

    private fun config() {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.configBible()

        sharedPreferences = applicationContext.getSharedPreferences("shared preferences", MODE_PRIVATE)
        themeIndex = sharedPreferences.getInt("themeIndex", 0)

        initObserver()
        initView()
    }

    private fun initObserver() {
        viewModel.todayVerse.observe(this) {
            (binding.recyclerView.adapter as MealPlanAdapter).setData(it)

            binding.recyclerView.post {
                binding.recyclerView.scrollToPosition(0)
            }
        }

        viewModel.todayDescription.observe(this) {
            binding.tvInfo.text = it
        }

        viewModel.scheduleDate.observe(this) {
            binding.tvDate.text = it
        }
        viewModel.scheduleList.observe(this) {
            Log.d("", it.toString())
        }
    }

    private fun initView() {

        when (themeIndex) {
            0 -> {
                changeTheme(AppCompatDelegate.MODE_NIGHT_NO)
            }

            1 -> {
                changeTheme(AppCompatDelegate.MODE_NIGHT_YES)
            }

            2 -> {
                changeTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = MealPlanAdapter(this) {
            Log.d("MainActivity","verse index: %s"+ it.toString())
        }

        _scheduleBottomSheetBinding = BottomSheetScheduleBinding.inflate(LayoutInflater.from(this),binding.root,false)
        _appInfoBottomSheetDialogBinding = BottomSheetAppInfoBinding.inflate(LayoutInflater.from(this),binding.root,false)

        bottomSheetDialog = BottomSheetDialog(this, R.style.bottom_sheet_dialog)

        binding.tvDate.setOnClickListener {
            scheduleBottomSheetBinding?.let { binding ->
                initScheduleList(binding)
            }
        }

        binding.scheduleButton.setOnClickListener {
            scheduleBottomSheetBinding?.let { binding ->
                initScheduleList(binding)
            }
        }

        binding.btnAppInfo.setOnClickListener {
            appInfoBottomSheetDialogBinding?.let { binding ->
                initAppInfoSheet(binding)
            }
        }
    }

    private fun initScheduleList(binding: BottomSheetScheduleBinding) {
        binding.rvSchedule.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val adapter = ScheduleListAdapter(this) {
            Log.d("MainActivity", "verse index: %s" + it.toString())

            viewModel.scheduleList.value?.getOrNull(it)?.let { plan ->
                Log.d("", plan.day ?: "")

                val planDate = plan.day.orEmpty()
                val date = Globals.convertStringToDate(planDate)

                date.let {
                    updateDateString(Globals.headerDateString(it))
                }

                viewModel.setDatePlan(plan)
            }

            bottomSheetDialog.dismiss()
        }

        val planList = viewModel.scheduleList.value.orEmpty()

        adapter.submitList(planList)

        binding.rvSchedule.adapter = adapter

        binding.rvSchedule.post {
            val searchIndex = viewModel.getTodayIndex(planList)
            if (searchIndex!=null) {
                val searchDayIndex = searchIndex + 1
                binding.rvSchedule.scrollToPosition(searchDayIndex)
            }
        }

        bottomSheetDialog.setContentView(binding.root)
        bottomSheetDialog.show()
    }

    private fun initAppInfoSheet(binding: BottomSheetAppInfoBinding) {
        binding.tvAppVersion.text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"

//        binding.tvFeedback.setOnClickListener(View.OnClickListener {
//            val email = Intent(Intent.ACTION_SEND)
//            email.setType("plain/text")
//            val address = arrayOf("echadworks@gmail.com")
//            email.putExtra(Intent.EXTRA_EMAIL, address)
////            email.putExtra(Intent.EXTRA_SUBJECT, "test@test")
////            email.putExtra(Intent.EXTRA_TEXT, "내용 미리보기 (미리적을 수 있음)")
//            startActivity(email)
//        })

       binding.radioGp.setOnCheckedChangeListener{ radioGroup, index ->
          var selectedIndex = 0
           when(index) {
               binding.radioLight.id->{
                   selectedIndex = 0
                   changeTheme(AppCompatDelegate.MODE_NIGHT_NO)
               }
               binding.radioDark.id->{
                   selectedIndex = 1
                   changeTheme(AppCompatDelegate.MODE_NIGHT_YES)
               }
               binding.radioSystem.id->{
                   selectedIndex = 2
                   changeTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
               }
           }
            themeIndex = selectedIndex
            val sharedPreferences: SharedPreferences = applicationContext.getSharedPreferences("shared preferences", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putInt("themeIndex", themeIndex)
            editor.apply()
        }

        initTheme(binding)

        bottomSheetDialog.setContentView(binding.root)
        bottomSheetDialog.show()
    }

    private fun initTheme(binding: BottomSheetAppInfoBinding) {
        themeIndex = sharedPreferences.getInt("themeIndex", 0)

        when (themeIndex) {
            0 -> {
                binding.radioLight.isChecked = true
                changeTheme(AppCompatDelegate.MODE_NIGHT_NO)
            }

            1 -> {
                binding.radioDark.isChecked = true
                changeTheme(AppCompatDelegate.MODE_NIGHT_YES)
            }

            2 -> {
                binding.radioSystem.isChecked = true
                changeTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    private fun updateDateString(date: String) {
        binding.tvDate.text = date
    }

    override fun onDestroy() {
        bottomSheetDialog.dismiss()
        _scheduleBottomSheetBinding = null
        _appInfoBottomSheetDialogBinding = null
        super.onDestroy()
    }

    private fun changeTheme(mode: Int) {
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    override fun onResume() {
        super.onResume()
        requestPermissions()
    }
}