package com.example.todolist.view.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
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
    private lateinit var btnClearCompleted: FloatingActionButton
    private lateinit var spinnerPriority: Spinner
    private lateinit var etSearch: EditText

    private val viewModel: TaskViewModel by viewModels()
    private var selectedCategoryId: Int? = null
    private var selectedPriority: String? = null

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
        btnClearCompleted = view.findViewById(R.id.btnClearCompleted)
        spinnerPriority = view.findViewById(R.id.spinnerPriority)
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

        btnClearCompleted.setOnClickListener {
            showClearCompletedDialog()
        }

        setupPrioritySpinner()

        return view
    }

    private fun setupRecyclerViews() {
        // Tasks
        taskAdapter = TaskAdapter(
            requireContext(),
            emptyList(),
            onDelete = { task -> viewModel.deleteTask(task) },
            onEdit = { task ->
                val intent = Intent(requireContext(), EditTaskActivity::class.java)
                intent.putExtra("taskId", task.id)
                startActivity(intent)
            },
            onChecked = { task, isChecked ->
                viewModel.updateTask(task.copy(isDone = isChecked))
            },
            onUndo = { task -> viewModel.insertTask(task) }
        )
        rvTasks.layoutManager = LinearLayoutManager(requireContext())
        rvTasks.adapter = taskAdapter

        // Categories
        categoryAdapter = CategoryBadgeAdapter(emptyList()) { category ->
            // Toggle category selection
            if (selectedCategoryId == category.id) {
                // Deselect if already selected
                selectedCategoryId = null
                categoryAdapter.setSelectedCategory(null)
                applyFilters()
            } else {
                // Select new category
                selectedCategoryId = category.id
                categoryAdapter.setSelectedCategory(category.id)
                applyFilters()
            }
        }
        rvCategories.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvCategories.adapter = categoryAdapter
    }

    private fun setupObservers() {
        // Only observe allTasks if no category is selected
        viewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            if (selectedCategoryId == null && selectedPriority == null) {
                taskAdapter.setData(tasks)
            }
        }

        viewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            categoryAdapter.setData(categories)
        }
    }

    private fun applyFilters() {
        when {
            selectedCategoryId != null && selectedPriority != null -> {
                // Both category and priority selected
                viewModel.getTasksByCategoryAndPriority(selectedCategoryId!!, selectedPriority!!)
                    .observe(viewLifecycleOwner) { tasks ->
                        taskAdapter.setData(tasks)
                    }
            }
            selectedCategoryId != null -> {
                // Only category selected
                viewModel.getTasksByCategory(selectedCategoryId!!)
                    .observe(viewLifecycleOwner) { tasks ->
                        taskAdapter.setData(tasks)
                    }
            }
            selectedPriority != null -> {
                // Only priority selected
                viewModel.getTasksByPriority(selectedPriority!!)
                    .observe(viewLifecycleOwner) { tasks ->
                        taskAdapter.setData(tasks)
                    }
            }
            else -> {
                // No filters selected, show all tasks
                viewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
                    taskAdapter.setData(tasks)
                }
            }
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

    private fun showClearCompletedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Clear Completed Tasks")
            .setMessage("Are you sure you want to delete all completed tasks?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.deleteAllCompletedTasks()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun setupPrioritySpinner() {
        val priorities = arrayOf("All", "Low", "Medium", "High")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, priorities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPriority.adapter = adapter

        spinnerPriority.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedPriority = when (position) {
                    0 -> null // All
                    1 -> "Low"
                    2 -> "Medium"
                    3 -> "High"
                    else -> null
                }
                applyFilters()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
                selectedPriority = null
                applyFilters()
            }
        }
    }
}
