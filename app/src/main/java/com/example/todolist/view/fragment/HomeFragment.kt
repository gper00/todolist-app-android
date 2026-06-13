package com.example.todolist.view.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.adapter.CategoryBadgeAdapter
import com.example.todolist.adapter.TaskAdapter
import com.example.todolist.view.AddTaskActivity
import com.example.todolist.view.EditTaskActivity
import com.example.todolist.view.ManageCategoriesActivity
import com.example.todolist.viewmodel.TaskViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {

    private lateinit var rvTasks: RecyclerView
    private lateinit var rvCategories: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var categoryAdapter: CategoryBadgeAdapter
    private lateinit var btnAddTask: FloatingActionButton
    private lateinit var btnAddCategoryQuick: ImageButton
    private lateinit var etSearch: EditText

    private val viewModel: TaskViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // INIT VIEW
        rvTasks = view.findViewById(R.id.recyclerViewTask)
        rvCategories = view.findViewById(R.id.rvCategoryBadges)
        btnAddTask = view.findViewById(R.id.btnAddTask)
        btnAddCategoryQuick = view.findViewById(R.id.btnAddCategoryQuick)
        etSearch = view.findViewById(R.id.etSearch)

        setupRecyclerViews()
        setupObservers()
        setupSearch()

        btnAddTask.setOnClickListener {
            startActivity(Intent(requireContext(), AddTaskActivity::class.java))
        }

        btnAddCategoryQuick.setOnClickListener {
            startActivity(Intent(requireContext(), ManageCategoriesActivity::class.java))
        }

        return view
    }

    private fun setupRecyclerViews() {
        // Tasks
        taskAdapter = TaskAdapter(
            emptyList(),
            onDelete = { task -> viewModel.deleteTask(task) },
            onEdit = { task ->
                val intent = Intent(requireContext(), EditTaskActivity::class.java)
                intent.putExtra("taskId", task.id)
                startActivity(intent)
            },
            onChecked = { task, isChecked ->
                viewModel.updateTask(task.copy(isDone = isChecked))
            }
        )
        rvTasks.layoutManager = LinearLayoutManager(requireContext())
        rvTasks.adapter = taskAdapter

        // Categories
        categoryAdapter = CategoryBadgeAdapter(emptyList()) { category ->
            // Option: Filter tasks by category
        }
        rvCategories.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvCategories.adapter = categoryAdapter
    }

    private fun setupObservers() {
        viewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            taskAdapter.setData(tasks)
        }

        viewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            categoryAdapter.setData(categories)
        }
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchTasks(s.toString()).observe(viewLifecycleOwner) { tasks ->
                    taskAdapter.setData(tasks)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
}
