package com.example.todolist.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.adapter.TaskAdapter
import com.example.todolist.database.AppDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var adapter: TaskAdapter

    private lateinit var btnAddTask: FloatingActionButton

    private lateinit var etSearch: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // INIT VIEW
        recyclerView = findViewById(R.id.recyclerViewTask)

        btnAddTask = findViewById(R.id.btnAddTask)

        etSearch = findViewById(R.id.etSearch)

        // DATABASE
        val db = AppDatabase.getDatabase(this)

        // ADAPTER
        adapter = TaskAdapter(

            emptyList(),

            // DELETE
            onDelete = { task ->

                lifecycleScope.launch {

                    db.taskDao().deleteTask(task)
                }
            },

            // EDIT
            onEdit = { task ->

                val intent =
                    Intent(
                        this,
                        EditTaskActivity::class.java
                    )

                intent.putExtra(
                    "taskId",
                    task.id
                )

                startActivity(intent)
            },

            // CHECKBOX
            onChecked = { task, isChecked ->

                lifecycleScope.launch {

                    db.taskDao().updateTask(

                        task.copy(
                            isDone = isChecked
                        )
                    )
                }
            }
        )

        // RECYCLERVIEW
        recyclerView.layoutManager =
            LinearLayoutManager(this)

        recyclerView.adapter = adapter

        // LOAD ALL TASK
        db.taskDao()
            .getAllTasks()
            .observe(this) {

                adapter.setData(it)
            }

        // SEARCH TASK
        etSearch.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    db.taskDao()
                        .searchTask(
                            s.toString()
                        )
                        .observe(this@MainActivity) {

                            adapter.setData(it)
                        }
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )

        // ADD TASK
        btnAddTask.setOnClickListener {

            startActivity(

                Intent(
                    this,
                    AddTaskActivity::class.java
                )
            )
        }
    }
}