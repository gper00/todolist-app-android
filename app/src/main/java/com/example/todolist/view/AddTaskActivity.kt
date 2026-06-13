package com.example.todolist.view

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.R
import com.example.todolist.model.Category
import com.example.todolist.model.Task
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
    private lateinit var spinnerPriority: Spinner
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnSave: MaterialButton

    private val calendar = Calendar.getInstance()
    private val viewModel: TaskViewModel by viewModels()
    private var categoriesList: List<Category> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        etTitle = findViewById(R.id.etTitle)
        etDescription = findViewById(R.id.etDescription)
        etDeadline = findViewById(R.id.etDeadline)
        spinnerPriority = findViewById(R.id.spinnerPriority)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        btnSave = findViewById(R.id.btnSave)

        etDeadline.setOnClickListener { showDatePicker() }

        // Priority List
        val priorityList = arrayOf("Low", "Medium", "High")
        spinnerPriority.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, priorityList)

        // Observe Categories from DB
        viewModel.allCategories.observe(this) { categories ->
            categoriesList = categories
            val categoryNames = categories.map { it.name }
            spinnerCategory.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categoryNames)
        }

        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val deadline = etDeadline.text.toString().trim()
            val priority = spinnerPriority.selectedItem.toString()
            
            if (categoriesList.isEmpty()) {
                Toast.makeText(this, "Please create a category first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedCategory = categoriesList[spinnerCategory.selectedItemPosition]

            if (title.isEmpty() || description.isEmpty() || deadline.isEmpty()) {
                Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val task = Task(
                title = title,
                description = description,
                deadline = deadline,
                priority = priority,
                categoryId = selectedCategory.id
            )

            viewModel.insertTask(task)
            Toast.makeText(this, "Task berhasil ditambahkan", Toast.LENGTH_SHORT).show()
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
        val format = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        etDeadline.setText(format.format(calendar.time))
    }
}
