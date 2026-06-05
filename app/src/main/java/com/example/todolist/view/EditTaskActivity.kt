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

    private lateinit var spinnerPriority: Spinner
    private lateinit var spinnerCategory: Spinner

    private lateinit var btnUpdate: MaterialButton

    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_task)

        // INPUT
        etTitle = findViewById(R.id.etTitle)
        etDescription = findViewById(R.id.etDescription)
        etDeadline = findViewById(R.id.etDeadline)

        // SPINNER
        spinnerPriority = findViewById(R.id.spinnerPriority)
        spinnerCategory = findViewById(R.id.spinnerCategory)

        // BUTTON
        btnUpdate = findViewById(R.id.btnUpdate)

        // DATABASE
        val db = AppDatabase.getDatabase(this)

        // TASK ID
        val taskId = intent.getIntExtra("taskId", 0)

        // PRIORITY LIST
        val priorityList = arrayOf(
            "Low",
            "Medium",
            "High"
        )

        // CATEGORY LIST
        val categoryList = arrayOf(
            "Work",
            "Study",
            "Personal",
            "Shopping"
        )

        // PRIORITY ADAPTER
        spinnerPriority.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            priorityList
        )

        // CATEGORY ADAPTER
        spinnerCategory.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categoryList
        )

        // DATE PICKER
        etDeadline.setOnClickListener {
            showDatePicker()
        }

        // LOAD TASK
        lifecycleScope.launch {

            val task = db.taskDao().getTaskById(taskId)

            runOnUiThread {

                etTitle.setText(task.title)

                etDescription.setText(task.description)

                etDeadline.setText(task.deadline)

                // SET SPINNER POSITION
                spinnerPriority.setSelection(
                    priorityList.indexOf(task.priority)
                )

                spinnerCategory.setSelection(
                    categoryList.indexOf(task.category)
                )
            }
        }

        // UPDATE BUTTON
        btnUpdate.setOnClickListener {

            val title = etTitle.text.toString().trim()

            val description =
                etDescription.text.toString().trim()

            val deadline =
                etDeadline.text.toString().trim()

            val priority =
                spinnerPriority.selectedItem.toString()

            val category =
                spinnerCategory.selectedItem.toString()

            // VALIDATION
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

            lifecycleScope.launch {

                val oldTask =
                    db.taskDao().getTaskById(taskId)

                db.taskDao().updateTask(

                    oldTask.copy(
                        title = title,
                        description = description,
                        deadline = deadline,
                        priority = priority,
                        category = category
                    )
                )

                runOnUiThread {

                    Toast.makeText(
                        this@EditTaskActivity,
                        "Task updated",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                }
            }
        }
    }

    // DATE PICKER
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

    // UPDATE DATE FIELD
    private fun updateDateField() {

        val format = SimpleDateFormat(
            "dd MMMM yyyy",
            Locale.getDefault()
        )

        etDeadline.setText(
            format.format(calendar.time)
        )
    }
}