package com.example.todolist.view

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.todolist.R
import com.example.todolist.database.AppDatabase
import com.example.todolist.model.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_task)

        // Input
        etTitle = findViewById(R.id.etTitle)
        etDescription = findViewById(R.id.etDescription)
        etDeadline = findViewById(R.id.etDeadline)

        // Spinner
        spinnerPriority = findViewById(R.id.spinnerPriority)
        spinnerCategory = findViewById(R.id.spinnerCategory)

        // Button
        btnSave = findViewById(R.id.btnSave)

        // Database
        val db = AppDatabase.getDatabase(this)

        // =========================
        // DATE PICKER
        // =========================

        etDeadline.setOnClickListener {

            showDatePicker()
        }

        // Priority List
        val priorityList = arrayOf(
            "Low",
            "Medium",
            "High"
        )

        // Category List
        val categoryList = arrayOf(
            "Work",
            "Study",
            "Personal",
            "Shopping"
        )

        // Priority Adapter
        spinnerPriority.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            priorityList
        )

        // Category Adapter
        spinnerCategory.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categoryList
        )

        // Save Button
        btnSave.setOnClickListener {

            val title = etTitle.text.toString().trim()

            val description =
                etDescription.text.toString().trim()

            val deadline =
                etDeadline.text.toString().trim()

            val priority =
                spinnerPriority.selectedItem.toString()

            val category =
                spinnerCategory.selectedItem.toString()

            // Validation
            if (
                title.isEmpty() ||
                description.isEmpty() ||
                deadline.isEmpty()
            ) {

                Toast.makeText(
                    this,
                    "Semua field wajib diisi",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // Create Task
            val task = Task(
                title = title,
                description = description,
                deadline = deadline,
                priority = priority,
                category = category
            )

            // Insert Database
            lifecycleScope.launch {

                db.taskDao().insertTask(task)

                runOnUiThread {

                    Toast.makeText(
                        this@AddTaskActivity,
                        "Task berhasil ditambahkan",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                }
            }
        }
    }

    // =========================
    // DATE PICKER FUNCTION
    // =========================

    private fun showDatePicker() {

        val datePickerDialog = DatePickerDialog(
            this,

            { _, year, month, dayOfMonth ->

                calendar.set(
                    Calendar.YEAR,
                    year
                )

                calendar.set(
                    Calendar.MONTH,
                    month
                )

                calendar.set(
                    Calendar.DAY_OF_MONTH,
                    dayOfMonth
                )

                updateDateField()
            },

            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    // =========================
    // FORMAT DATE
    // =========================

    private fun updateDateField() {

        val format =
            SimpleDateFormat(
                "dd MMMM yyyy",
                Locale.getDefault()
            )

        etDeadline.setText(
            format.format(calendar.time)
        )
    }
}