package com.echadworks.meal.view.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.echadworks.meal.databinding.ReadingPlanItemBinding
import com.echadworks.meal.network.Plan
import com.echadworks.meal.utils.Globals

class PlanAdapter(private val onItemClicked: (Int) -> Unit): ListAdapter<Plan, PlanAdapter.ViewHolder>(diffUtil) {
    inner class ViewHolder(private val binding: ReadingPlanItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Plan) {
            val selectedDay = data.day?.let { Globals.changeSelectedDate(it) }

            binding.tvScheme.text = selectedDay
            binding.tvDisc.text = String.format(
                "%s %s:%s-%s:%s",
                data.book?.let { Globals.getBook(it).name },
                data.fChap.toString(),
                data.fVer.toString(),
                data.lChap.toString(),
                data.lVer.toString()
            )

            val position = bindingAdapterPosition

            itemView.setOnClickListener {
                if (position != RecyclerView.NO_POSITION) {
                    onItemClicked(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ReadingPlanItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item)
    }

    companion object {
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