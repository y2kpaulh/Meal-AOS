package com.echadworks.meal.view.list

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.echadworks.meal.databinding.ReadingPlanItemBinding
import com.echadworks.meal.network.Plan

class PlanAdapter(private val onItemClick: (Plan, Int) -> Unit): ListAdapter<Plan, PlanAdapter.ViewHolder>(diffUtil) {
    inner class ViewHolder(private val binding: ReadingPlanItemBinding, onItemClicked: (Int) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                // this will be called only once.
                onItemClicked(bindingAdapterPosition)
            }
        }

        fun bind(data: Plan) {
            binding.tvScheme.text = data.day
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ReadingPlanItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ) {
            onItemClick(currentList[it], it)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item)
    }

    companion object {
        // diffUtil: currentList에 있는 각 아이템들을 비교하여 최신 상태를 유지하도록 한다.
        val diffUtil = object : DiffUtil.ItemCallback<Plan>() {
            override fun areItemsTheSame(oldItem: Plan, newItem: Plan): Boolean {
                return oldItem.day == newItem.day
            }

            override fun areContentsTheSame(oldItem: Plan, newItem: Plan): Boolean {
                return oldItem == newItem
            }
        }
    }
}