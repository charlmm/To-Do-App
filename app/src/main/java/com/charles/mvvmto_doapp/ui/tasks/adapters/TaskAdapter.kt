package com.charles.mvvmto_doapp.ui.tasks.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.charles.mvvmto_doapp.data.Task
import com.charles.mvvmto_doapp.databinding.ItemTaskBinding

class TaskAdapter(private val listener: OnItemClickListener) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(private val binding: ItemTaskBinding): RecyclerView.ViewHolder(binding.root){
        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        val task = differ.currentList[position]
                        listener.onItemClicked(task)
                    }
                }
                checkBoxCompleted.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val task = differ.currentList[position]
                        listener.onCheckboxClicked(task, checkBoxCompleted.isChecked)
                    }
                }
            }
        }
        fun bind(task: Task) {
            binding.apply {
                checkBoxCompleted.isChecked = task.completed
                tvName.text = task.name
                tvName.paint.isStrikeThruText = task.completed
                icPriority.isVisible = task.important
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            ItemTaskBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false)
        )
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    interface OnItemClickListener {
        fun onItemClicked(task: Task)
        fun onCheckboxClicked(task: Task, isChecked: Boolean)
    }

    private val diffCallBack =  object: DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    val differ = AsyncListDiffer(this, diffCallBack)
}