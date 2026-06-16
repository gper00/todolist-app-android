package com.example.todolist.view

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.todolist.R
import com.example.todolist.database.AppDatabase
import com.example.todolist.model.Category
import com.example.todolist.viewmodel.TaskViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditTaskActivity : AppCompatActivity() {

    private lateinit var etTitle: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var etDeadline: TextInputEditText
    private lateinit var autoCompletePriority: AutoCompleteTextView
    private lateinit var autoCompleteCategory: AutoCompleteTextView
    private lateinit var btnUpdate: MaterialButton

    private val calendar = Calendar.getInstance()
    private val viewModel: TaskViewModel by viewModels()
    private var categoriesList: List<Category> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)

        etTitle = findViewById(R.id.etTitle)
        etDescription = findViewById(R.id.etDescription)
        etDeadline = findViewById(R.id.etDeadline)
        autoCompletePriority = findViewById(R.id.autoCompletePriority)
        autoCompleteCategory = findViewById(R.id.autoCompleteCategory)
        btnUpdate = findViewById(R.id.btnUpdate)

        val taskId = intent.getIntExtra("taskId", 0)

        // Date Picker
        etDeadline.setOnClickListener { showDatePicker() }

        // Priority List
        val priorityList = arrayOf("Low", "Medium", "High")
        autoCompletePriority.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, priorityList))

        // Observe Categories
        viewModel.allCategories.observe(this) { categories ->
            categoriesList = categories
            val categoryNames = categories.map { it.name }
            autoCompleteCategory.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, categoryNames))
            
            // Re-load task once categories are available
            loadTask(taskId)
        }

        btnUpdate.setOnClickListener {
            updateTask(taskId)
        }
    }

    private fun loadTask(taskId: Int) {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@EditTaskActivity)
            val taskWithCategory = db.taskDao().getTaskById(taskId)
            val task = taskWithCategory.task
            val category = taskWithCategory.category
            
            runOnUiThread {
                etTitle.setText(task.title)
                etDescription.setText(task.description)
                etDeadline.setText(task.deadline)
                autoCompletePriority.setText(task.priority, false)
                category?.let {
                    autoCompleteCategory.setText(it.name, false)
                }
            }
        }
    }

    private fun updateTask(taskId: Int) {
        val title = etTitle.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val deadline = etDeadline.text.toString().trim()
        val priority = autoCompletePriority.text.toString().trim()
        val categoryName = autoCompleteCategory.text.toString().trim()

        if (title.isEmpty()) {
            etTitle.error = "Task title is required"
            return
        }

        val selectedCategory = categoriesList.find { it.name == categoryName }

        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@EditTaskActivity)
            val taskWithCategory = db.taskDao().getTaskById(taskId)
            val oldTask = taskWithCategory.task
            
            viewModel.updateTask(oldTask.copy(
                title = title,
                description = description,
                deadline = deadline,
                priority = priority,
                categoryId = selectedCategory?.id
            ))
            
            runOnUiThread {
                Toast.makeText(this@EditTaskActivity, "Task updated", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun showDatePicker() {
        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            updateDateField()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun updateDateField() {
        val format = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        etDeadline.setText(format.format(calendar.time))
    }
}
