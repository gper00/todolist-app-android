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
import java.text.SimpleDateFormat
import java.util.Locale

class TaskDetailActivity : AppCompatActivity() {

    private lateinit var tvTitle: TextView
    private lateinit var tvCategory: TextView
    private lateinit var cardCategory: MaterialCardView
    private lateinit var tvPriority: TextView
    private lateinit var tvDeadline: TextView
    private lateinit var tvDescription: TextView
    private lateinit var layoutDeadline: View
    private lateinit var btnEdit: FloatingActionButton

    // Formatters for normalization
    private val idLocale = Locale("id", "ID")
    private val idFormat = SimpleDateFormat("dd MMMM yyyy", idLocale)
    private val enFormat = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

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

    private fun normalizeDate(dateStr: String?): String {
        if (dateStr.isNullOrEmpty()) return ""
        try {
            idFormat.parse(dateStr)
            return dateStr
        } catch (e: Exception) {
            try {
                val date = enFormat.parse(dateStr)
                if (date != null) return idFormat.format(date)
            } catch (e2: Exception) {
                try {
                    val date = isoFormat.parse(dateStr)
                    if (date != null) return idFormat.format(date)
                } catch (e3: Exception) {}
            }
        }
        return dateStr
    }

    private fun loadTaskDetails(taskId: Int) {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@TaskDetailActivity)
            val taskWithCategory = db.taskDao().getTaskById(taskId)
            val task = taskWithCategory.task
            val category = taskWithCategory.category

            runOnUiThread {
                tvTitle.text = task.title
                
                if (category != null) {
                    cardCategory.visibility = View.VISIBLE
                    tvCategory.text = category.name
                    try {
                        val color = Color.parseColor(category.color)
                        cardCategory.setCardBackgroundColor(color)
                        cardCategory.strokeWidth = 0
                        val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
                        tvCategory.setTextColor(if (darkness < 0.5) Color.BLACK else Color.WHITE)
                    } catch (e: Exception) {
                        cardCategory.setCardBackgroundColor(Color.GRAY)
                        tvCategory.setTextColor(Color.WHITE)
                    }
                } else {
                    cardCategory.visibility = View.GONE
                }

                if (task.priority.isNotEmpty()) {
                    tvPriority.visibility = View.VISIBLE
                    tvPriority.text = task.priority
                } else {
                    tvPriority.visibility = View.GONE
                }

                if (task.deadline.isNotEmpty()) {
                    layoutDeadline.visibility = View.VISIBLE
                    tvDeadline.text = normalizeDate(task.deadline)
                } else {
                    layoutDeadline.visibility = View.GONE
                }

                if (task.description.isNotEmpty()) {
                    tvDescription.text = task.description
                } else {
                    tvDescription.text = "No description provided."
                }
            }
        }
    }
}
