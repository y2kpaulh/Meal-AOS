package com.echadworks.meal

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.children
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.echadworks.meal.databinding.MealPlanItemBinding
import com.echadworks.meal.model.Verse

class MealPlanAdapter(private val context: Context, private val clickListener: (Int) -> Unit): ListAdapter<Verse, MealPlanAdapter.ViewHolder>(
    differ) {
    companion object {
        val differ = object : DiffUtil.ItemCallback<Verse>() {
            override fun areItemsTheSame(oldItem: Verse, newItem: Verse): Boolean {
                return oldItem.number == newItem.number
            }

            override fun areContentsTheSame(oldItem: Verse, newItem: Verse): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = MealPlanItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        clearFocus(binding.root)
        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: MealPlanItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Verse, position: Int) {
            binding.verseNum.text = data.number.toString()
            binding.verseText.text = data.text
            binding.root.setOnClickListener {
                clickListener(position)
            }
        }
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.setBackgroundColor(Color.TRANSPARENT)
        holder.bind(item,position)
    }

    fun setData(newData: ArrayList<Verse>) {
        submitList(newData)
    }

    private fun clearFocus(view: View) {
        if (view is ImageView) {
            view.isFocusable = false
            view.clearFocus()
        }
        if (view is ViewGroup) {
            view.children.forEach {
                clearFocus(it)
            }
        }
    }
}