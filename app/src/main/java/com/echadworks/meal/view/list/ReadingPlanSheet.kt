package com.echadworks.meal.view.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.echadworks.meal.databinding.ReadingPlanSheetBinding
import com.echadworks.meal.network.Plan
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ReadingPlanSheet: BottomSheetDialogFragment()
{
    private lateinit var binding: ReadingPlanSheetBinding
    private lateinit var adapter: PlanAdapter

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
            recyclerView.adapter = adapter

            buttonBottomSheet.setOnClickListener {
                Toast.makeText(requireContext(), "Bottom Sheet 안의 버튼 클릭", Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }
    }

    fun setAdapter(planAdapter: PlanAdapter) {
        adapter = planAdapter
    }

    fun setPlanList(list: List<Plan>) {
        adapter.submitList(list)
    }
}