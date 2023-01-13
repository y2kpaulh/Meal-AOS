package com.echadworks.meal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.echadworks.meal.databinding.MealPlanItemBinding
import com.echadworks.meal.model.Verse

class MealPlanAdapter(private var dataSet: ArrayList<Verse>): RecyclerView.Adapter<MealPlanAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = MealPlanItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    class ViewHolder(private val binding: MealPlanItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Verse) {
            binding.verseNum.text = data.number.toString()
            binding.verseText.text = data.text
        }
    }
    override fun onBindViewHolder(holder: MealPlanAdapter.ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun setData(newData: ArrayList<Verse>) {
        dataSet = newData
        notifyDataSetChanged()
    }

}