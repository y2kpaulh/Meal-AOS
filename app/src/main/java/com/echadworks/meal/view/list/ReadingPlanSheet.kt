package com.echadworks.meal.view.list

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.echadworks.meal.R
import com.echadworks.meal.databinding.ReadingPlanSheetBinding
import com.echadworks.meal.network.Plan
import com.echadworks.meal.view.main.MainViewModel.Companion.TAG
import com.echadworks.meal.view.main.MealPlanAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ReadingPlanSheet: BottomSheetDialogFragment()
{
    private lateinit var binding: ReadingPlanSheetBinding

    private var scrollToIndex: Int = 0
    private lateinit var dataList: List<Plan>

    var onItemSelectedListener: ((Int) -> Unit)? = null
    companion object {
        private const val ARG_DATA_LIST = "dataList"
        private const val ARG_SELECTED_INDEX = "selectedIndex"

        fun newInstance(selectedIndex: Int, dataList: List<Plan>): ReadingPlanSheet {
            return ReadingPlanSheet().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_DATA_LIST, ArrayList(dataList))
                    putInt(ARG_SELECTED_INDEX, selectedIndex)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)

        arguments?.let {
            scrollToIndex = it.getInt(ARG_SELECTED_INDEX)

            dataList = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable(ARG_DATA_LIST, ArrayList<Plan>()::class.java) ?: emptyList()
            } else {
                it.getParcelableArrayList<Plan>(ARG_DATA_LIST) ?: emptyList()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View?
    {
        binding = ReadingPlanSheetBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            val adapter = PlanAdapter { position ->
                onItemSelectedListener?.invoke(position)
            }

            adapter.submitList(dataList)
            recyclerView.adapter = adapter

            recyclerView.scrollToPosition(scrollToIndex)

        }
    }
}