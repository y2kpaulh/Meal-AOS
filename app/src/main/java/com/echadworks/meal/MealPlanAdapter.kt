package com.echadworks.meal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.echadworks.meal.databinding.MealPlanItemBinding

class MealPlanAdapter(private var dataSet: List<String>): RecyclerView.Adapter<MealPlanAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = MealPlanItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    class ViewHolder(private val binding: MealPlanItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: String) {
            binding.verseText.text = data
        }
    }
    override fun onBindViewHolder(holder: MealPlanAdapter.ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun setData(newData: List<String>) {
        dataSet = newData
        notifyDataSetChanged()
    }

}