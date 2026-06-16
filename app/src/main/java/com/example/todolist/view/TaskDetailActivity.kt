package com.example.todolist.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.todolist.R
import com.example.todolist.database.AppDatabase
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class TaskDetailActivity : AppCompatActivity() {

    private lateinit var tvTitle: TextView
    private lateinit var tvCategory: TextView
    private lateinit var cardCategory: MaterialCardView
    private lateinit var tvPriority: TextView
    private lateinit var tvDeadline: TextView
    private lateinit var tvDescription: TextView
    private lateinit var layoutDeadline: View
    private lateinit var btnEdit: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        tvTitle = findViewById(R.id.tvDetailTitle)
        tvCategory = findViewById(R.id.tvDetailCategory)
        cardCategory = findViewById(R.id.cardCategory)
        tvPriority = findViewById(R.id.tvDetailPriority)
        tvDeadline = findViewById(R.id.tvDetailDeadline)
        tvDescription = findViewById(R.id.tvDetailDescription)
        layoutDeadline = findViewById(R.id.layoutDeadline)
        btnEdit = findViewById(R.id.btnEditTaskDetail)

        val taskId = intent.getIntExtra("taskId", -1)
        if (taskId != -1) {
            loadTaskDetails(taskId)
        } else {
            finish()
        }

        btnEdit.setOnClickListener {
            val intent = Intent(this, EditTaskActivity::class.java)
            intent.putExtra("taskId", taskId)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val taskId = intent.getIntExtra("taskId", -1)
        if (taskId != -1) loadTaskDetails(taskId)
    }

    private fun loadTaskDetails(taskId: Int) {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@TaskDetailActivity)
            val taskWithCategory = db.taskDao().getTaskById(taskId)
            val task = taskWithCategory.task
            val category = taskWithCategory.category

            runOnUiThread {
                tvTitle.text = task.title
                
                // Category
                if (category != null) {
                    cardCategory.visibility = View.VISIBLE
                    tvCategory.text = category.name
                    try {
                        val color = Color.parseColor(category.color)
                        cardCategory.setCardBackgroundColor(color)
                        cardCategory.strokeWidth = 0 // Remove stroke for standard look
                        
                        // Dynamic text color for contrast
                        val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
                        tvCategory.setTextColor(if (darkness < 0.5) Color.BLACK else Color.WHITE)
                    } catch (e: Exception) {
                        cardCategory.setCardBackgroundColor(Color.GRAY)
                        tvCategory.setTextColor(Color.WHITE)
                    }
                } else {
                    cardCategory.visibility = View.GONE
                }

                // Priority
                if (task.priority.isNotEmpty()) {
                    tvPriority.visibility = View.VISIBLE
                    tvPriority.text = task.priority
                } else {
                    tvPriority.visibility = View.GONE
                }

                // Deadline
                if (task.deadline.isNotEmpty()) {
                    layoutDeadline.visibility = View.VISIBLE
                    tvDeadline.text = task.deadline
                } else {
                    layoutDeadline.visibility = View.GONE
                }

                // Description
                if (task.description.isNotEmpty()) {
                    tvDescription.text = task.description
                } else {
                    tvDescription.text = "No description provided."
                }
            }
        }
    }
}
