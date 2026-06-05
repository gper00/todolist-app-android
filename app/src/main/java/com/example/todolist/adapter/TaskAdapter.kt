package com.example.todolist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.model.Task

class TaskAdapter(
    private var taskList: List<Task>,
    private val onDelete: (Task) -> Unit,
    private val onEdit: (Task) -> Unit,
    private val onChecked: (Task, Boolean) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView) {

        val tvTitle: TextView =
            itemView.findViewById(R.id.tvTitle)

        val tvDeadline: TextView =
            itemView.findViewById(R.id.tvDeadline)

        val tvPriority: TextView =
            itemView.findViewById(R.id.tvPriority)

        val checkDone: CheckBox =
            itemView.findViewById(R.id.checkDone)

        val btnDelete: ImageButton =
            itemView.findViewById(R.id.btnDelete)

        val btnEdit: ImageButton =
            itemView.findViewById(R.id.btnEdit)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)

        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int = taskList.size

    override fun onBindViewHolder(
        holder: TaskViewHolder,
        position: Int
    ) {

        val task = taskList[position]

        holder.tvTitle.text = task.title
        holder.tvDeadline.text = task.deadline
        holder.tvPriority.text = task.priority

        holder.checkDone.isChecked = task.isDone

        holder.btnDelete.setOnClickListener {
            onDelete(task)
        }

        holder.btnEdit.setOnClickListener {
            onEdit(task)
        }

        holder.checkDone.setOnCheckedChangeListener { _, isChecked ->
            onChecked(task, isChecked)
        }
    }

    fun setData(newList: List<Task>) {
        taskList = newList
        notifyDataSetChanged()
    }
}