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
import androidx.recyclerview.widget.DiffUtil
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
        
        if (task.priority.isNotEmpty()) {
            holder.tvPriority.visibility = View.VISIBLE
            holder.tvPriority.text = task.priority
        } else {
            holder.tvPriority.visibility = View.GONE
        }

        holder.checkDone.setOnCheckedChangeListener(null)
        holder.checkDone.isChecked = task.isDone

        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.RECTANGLE
        try {
            val colorStr = category?.color ?: "#E0E0E0"
            drawable.setColor(Color.parseColor(colorStr))
        } catch (e: Exception) {
            drawable.setColor(Color.LTGRAY)
        }
        holder.viewCategoryColor.background = drawable

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, TaskDetailActivity::class.java)
            intent.putExtra("taskId", task.id)
            it.context.startActivity(intent)
        }

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

        holder.checkDone.setOnCheckedChangeListener { _, isChecked ->
            onChecked(task, isChecked)
        }
    }

    fun setData(newList: List<TaskWithCategory>) {
        val diffCallback = TaskDiffCallback(taskList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        taskList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    private fun showUndoSnackbar(task: Task, view: View) {
        Snackbar.make(view, "Task deleted", Snackbar.LENGTH_LONG)
            .setAction("Undo") {
                onUndo(task)
            }
            .show()
    }

    class TaskDiffCallback(
        private val oldList: List<TaskWithCategory>,
        private val newList: List<TaskWithCategory>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].task.id == newList[newItemPosition].task.id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
