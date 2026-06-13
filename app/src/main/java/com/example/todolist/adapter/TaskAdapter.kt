package com.example.todolist.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.model.Task
import com.example.todolist.model.TaskWithCategory

class TaskAdapter(
    private var taskList: List<TaskWithCategory>,
    private val onDelete: (Task) -> Unit,
    private val onEdit: (Task) -> Unit,
    private val onChecked: (Task, Boolean) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvPriority: TextView = itemView.findViewById(R.id.tvPriority)
        val viewCategoryColor: View = itemView.findViewById(R.id.viewCategoryColor)
        val checkDone: CheckBox = itemView.findViewById(R.id.checkDone)
        val btnOptions: ImageButton = itemView.findViewById(R.id.btnOptions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int = taskList.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val taskWithCategory = taskList[position]
        val task = taskWithCategory.task
        val category = taskWithCategory.category

        holder.tvTitle.text = task.title
        holder.tvPriority.text = task.priority

        // Fix CheckBox recycling bug: detach listener before setting isChecked
        holder.checkDone.setOnCheckedChangeListener(null)
        holder.checkDone.isChecked = task.isDone

        // Set Category Color (Left Strip)
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.RECTANGLE
        try {
            drawable.setColor(Color.parseColor(category?.color ?: "#666666"))
        } catch (e: Exception) {
            drawable.setColor(Color.GRAY)
        }
        holder.viewCategoryColor.background = drawable

        // Options Menu
        holder.btnOptions.setOnClickListener { view ->
            val popup = PopupMenu(view.context, view)
            popup.menu.add("Edit")
            popup.menu.add("Delete")
            popup.setOnMenuItemClickListener { item ->
                when (item.title) {
                    "Edit" -> onEdit(task)
                    "Delete" -> onDelete(task)
                }
                true
            }
            popup.show()
        }

        // Re-attach listener after setting the checked state
        holder.checkDone.setOnCheckedChangeListener { _, isChecked ->
            onChecked(task, isChecked)
        }
    }

    fun setData(newList: List<TaskWithCategory>) {
        taskList = newList
        notifyDataSetChanged()
    }
}
