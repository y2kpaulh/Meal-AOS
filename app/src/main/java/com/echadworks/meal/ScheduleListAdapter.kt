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
import com.echadworks.meal.databinding.ItemScheduleListBinding
import com.echadworks.meal.model.Verse
import com.echadworks.meal.network.Plan
import com.echadworks.meal.utils.Globals

class ScheduleListAdapter(private val context: Context, private val clickListener: (Int) -> Unit): ListAdapter<Plan, ScheduleListAdapter.ViewHolder>(
    differ) {
    companion object {
        val differ = object : DiffUtil.ItemCallback<Plan>() {
            override fun areItemsTheSame(oldItem: Plan, newItem: Plan): Boolean {
                return oldItem.day == newItem.day
            }

            override fun areContentsTheSame(oldItem: Plan, newItem: Plan): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemScheduleListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        clearFocus(binding.root)
        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: ItemScheduleListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Plan, position: Int) {

            val planDate = data.day.orEmpty()
            val date = Globals.convertStringToDate(planDate)

            date?.let { dateStr ->
                binding.tvDate.text = Globals.dateString(dateStr)
            }

            binding.tvMeal.text = String.format(
                "%s %s:%s-%s:%s",
                data.book,
                data.fChap.toString(),
                data.fVer.toString(),
                data.lChap.toString(),
                data.lVer.toString()
            )

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

    fun setData(newData: ArrayList<Plan>) {
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