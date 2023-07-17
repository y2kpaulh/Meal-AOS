package com.echadworks.meal.view.main

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.echadworks.meal.databinding.BibleWordItemBinding
import com.echadworks.meal.model.Verse

class MealPlanAdapter: ListAdapter<Verse, MealPlanAdapter.ViewHolder>(diffUtil) {
    inner class ViewHolder(private val binding: BibleWordItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Verse) {
            binding.verseNum.text = data.number.toString()
            binding.verseText.text = data.text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            BibleWordItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
        holder.itemView.setBackgroundColor(Color.TRANSPARENT)
    }

    companion object {
        // diffUtil: currentList에 있는 각 아이템들을 비교하여 최신 상태를 유지하도록 한다.
        val diffUtil = object : DiffUtil.ItemCallback<Verse>() {
            override fun areItemsTheSame(oldItem: Verse, newItem: Verse): Boolean {
                return oldItem.text == newItem.text
            }

            override fun areContentsTheSame(oldItem: Verse, newItem: Verse): Boolean {
                return oldItem == newItem
            }
        }
    }
}