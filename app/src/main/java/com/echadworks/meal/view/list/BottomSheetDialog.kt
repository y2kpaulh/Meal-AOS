package com.echadworks.meal.view.list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.echadworks.meal.R
import com.echadworks.meal.network.Plan
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetDialog(private val adapter: PlanAdapter) : BottomSheetDialogFragment()
{
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.bottom_sheet_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<RecyclerView>(R.id.recyclerView).adapter = adapter

        view.findViewById<Button>(R.id.button_bottom_sheet)?.setOnClickListener {
            Toast.makeText(requireContext(), "Bottom Sheet 안의 버튼 클릭", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }
}