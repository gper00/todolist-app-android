package com.example.todolist.view.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.adapter.TaskAdapter
import com.example.todolist.view.AddTaskActivity
import com.example.todolist.view.EditTaskActivity
import com.example.todolist.viewmodel.TaskViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskAdapter
    private lateinit var btnAddTask: FloatingActionButton
    private lateinit var etSearch: EditText

    private val viewModel: TaskViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // INIT VIEW
        recyclerView = view.findViewById(R.id.recyclerViewTask)
        btnAddTask = view.findViewById(R.id.btnAddTask)
        etSearch = view.findViewById(R.id.etSearch)

        setupRecyclerView()
        setupObservers()
        setupSearch()

        btnAddTask.setOnClickListener {
            startActivity(Intent(requireContext(), AddTaskActivity::class.java))
        }

        return view
    }

    private fun setupRecyclerView() {
        adapter = TaskAdapter(
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
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            adapter.setData(tasks)
        }
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchTasks(s.toString()).observe(viewLifecycleOwner) { tasks ->
                    adapter.setData(tasks)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
}
