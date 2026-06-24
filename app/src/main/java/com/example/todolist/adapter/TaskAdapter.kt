package com.example.todolist.adapter

import android.content.Context
import android.content.Intent
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
import com.example.todolist.view.TaskDetailActivity
import com.google.android.material.snackbar.Snackbar

class TaskAdapter(
    private val context: Context,
    private var taskList: List<TaskWithCategory>,
    private val onDelete: (Task) -> Unit,
    private val onEdit: (Task) -> Unit,
    private val onChecked: (Task, Boolean) -> Unit,
    private val onUndo: (Task) -> Unit
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
        
        // Handle Priority Visibility
        if (task.priority.isNotEmpty()) {
            holder.tvPriority.visibility = View.VISIBLE
            holder.tvPriority.text = task.priority
        } else {
            holder.tvPriority.visibility = View.GONE
        }

        // Fix CheckBox recycling bug
        holder.checkDone.setOnCheckedChangeListener(null)
        holder.checkDone.isChecked = task.isDone

        // Set Category Color (Left Strip)
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.RECTANGLE
        try {
            val colorStr = category?.color ?: "#E0E0E0" // Default light gray if no category
            drawable.setColor(Color.parseColor(colorStr))
        } catch (e: Exception) {
            drawable.setColor(Color.LTGRAY)
        }
        holder.viewCategoryColor.background = drawable

        // Navigate to Details on Click
        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, TaskDetailActivity::class.java)
            intent.putExtra("taskId", task.id)
            it.context.startActivity(intent)
        }

        // Options Menu
        holder.btnOptions.setOnClickListener { view ->
            val popup = PopupMenu(view.context, view)
            popup.menu.add("Edit")
            popup.menu.add("Delete")
            popup.setOnMenuItemClickListener { item ->
                when (item.title) {
                    "Edit" -> onEdit(task)
                    "Delete" -> {
                        onDelete(task)
                        showUndoSnackbar(task, holder.itemView)
                    }
                }
                true
            }
            popup.show()
        }

        // Re-attach listener
        holder.checkDone.setOnCheckedChangeListener { _, isChecked ->
            onChecked(task, isChecked)
        }
    }

    fun setData(newList: List<TaskWithCategory>) {
        taskList = newList
        notifyDataSetChanged()
    }

    private fun showUndoSnackbar(task: Task, view: View) {
        Snackbar.make(view, "Task deleted", Snackbar.LENGTH_LONG)
            .setAction("Undo") {
                onUndo(task)
            }
            .show()
    }
}
