package com.example.todolist.view

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.R
import com.example.todolist.model.Category
import com.example.todolist.model.Task
import com.example.todolist.utils.ThemeStorage
import com.example.todolist.viewmodel.TaskViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTaskActivity : AppCompatActivity() {

    private lateinit var etTitle: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var etDeadline: TextInputEditText
    private lateinit var autoCompletePriority: AutoCompleteTextView
    private lateinit var autoCompleteCategory: AutoCompleteTextView
    private lateinit var btnSave: MaterialButton
    private lateinit var themeStorage: ThemeStorage

    private val calendar = Calendar.getInstance()
    private val viewModel: TaskViewModel by viewModels()
    private var categoriesList: List<Category> = emptyList()
    
    private val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

    override fun onCreate(savedInstanceState: Bundle?) {
        // Terapkan tema sebelum super.onCreate
        themeStorage = ThemeStorage(this)
        setTheme(themeStorage.getThemeResource())
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        etTitle = findViewById(R.id.etTitle)
        etDescription = findViewById(R.id.etDescription)
        etDeadline = findViewById(R.id.etDeadline)
        autoCompletePriority = findViewById(R.id.autoCompletePriority)
        autoCompleteCategory = findViewById(R.id.autoCompleteCategory)
        btnSave = findViewById(R.id.btnSave)

        etDeadline.setOnClickListener { showDatePicker() }

        val priorityList = arrayOf("Low", "Medium", "High")
        autoCompletePriority.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, priorityList))

        viewModel.allCategories.observe(this) { categories ->
            categoriesList = categories
            val categoryNames = categories.map { it.name }
            autoCompleteCategory.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, categoryNames))
        }

        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val deadline = etDeadline.text.toString().trim()
            val priority = autoCompletePriority.text.toString().trim()
            
            if (title.isEmpty()) {
                etTitle.error = "Task title is required"
                return@setOnClickListener
            }

            val categoryName = autoCompleteCategory.text.toString()
            val selectedCategory = categoriesList.find { it.name == categoryName }

            val task = Task(
                title = title,
                description = description,
                deadline = deadline,
                priority = priority,
                categoryId = selectedCategory?.id
            )

            viewModel.insertTask(task)
            Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun showDatePicker() {
        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            updateDateField()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun updateDateField() {
        etDeadline.setText(dateFormat.format(calendar.time))
    }
}
