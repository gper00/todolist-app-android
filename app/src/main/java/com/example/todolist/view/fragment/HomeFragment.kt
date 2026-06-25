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
import java.text.SimpleDateFormat
import java.util.Locale

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
    
    // Indonesia Locale consistency
    private val idLocale = Locale("id", "ID")
    private val dateFormat = SimpleDateFormat("dd MMMM yyyy", idLocale)
    private val enFormat = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

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

    private fun normalizeDate(dateStr: String?): String {
        if (dateStr.isNullOrEmpty()) return ""
        try {
            dateFormat.parse(dateStr)
            return dateStr 
        } catch (e: Exception) {
            try {
                val date = enFormat.parse(dateStr)
                if (date != null) return dateFormat.format(date)
            } catch (e2: Exception) {
                try {
                    val date = isoFormat.parse(dateStr)
                    if (date != null) return dateFormat.format(date)
                } catch (e3: Exception) {}
            }
        }
        return dateStr
    }

    private fun setupRecyclerViews() {
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

        categoryAdapter = CategoryBadgeAdapter(emptyList()) { category ->
            if (selectedCategoryId == category.id) {
                selectedCategoryId = null
                categoryAdapter.setSelectedCategory(null)
            } else {
                selectedCategoryId = category.id
                categoryAdapter.setSelectedCategory(category.id)
            }
            applyFilters()
        }
        rvCategories.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvCategories.adapter = categoryAdapter
    }

    private fun setupObservers() {
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
        if (selectedCategoryId == null && selectedPriority == null) {
            viewModel.allTasks.observe(viewLifecycleOwner) { taskAdapter.setData(it) }
            return
        }
        
        val liveData = when {
            selectedCategoryId != null && selectedPriority != null -> 
                viewModel.getTasksByCategoryAndPriority(selectedCategoryId!!, selectedPriority!!)
            selectedCategoryId != null -> 
                viewModel.getTasksByCategory(selectedCategoryId!!)
            else -> 
                viewModel.getTasksByPriority(selectedPriority!!)
        }
        
        liveData.observe(viewLifecycleOwner) { taskAdapter.setData(it) }
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
            .setTitle("Hapus Tugas Selesai")
            .setMessage("Yakin ingin menghapus semua tugas yang sudah selesai?")
            .setPositiveButton("Ya") { _, _ -> viewModel.deleteAllCompletedTasks() }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun setupPrioritySpinner() {
        val priorities = arrayOf("Semua Prioritas", "Low", "Medium", "High")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, priorities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPriority.adapter = adapter

        spinnerPriority.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedPriority = if (position == 0) null else priorities[position]
                applyFilters()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }
}
