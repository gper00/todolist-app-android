package com.example.todolist.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.adapter.TaskAdapter
import com.example.todolist.view.EditTaskActivity
import com.example.todolist.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var rvTasks: RecyclerView
    private lateinit var adapter: TaskAdapter
    private lateinit var tvSelectedDate: TextView

    private val viewModel: TaskViewModel by viewModels()
    private val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        calendarView = view.findViewById(R.id.calendarView)
        rvTasks = view.findViewById(R.id.rvCalendarTasks)
        tvSelectedDate = view.findViewById(R.id.tvSelectedDate)

        setupRecyclerView()

        val calendar = Calendar.getInstance()
        updateTasksForDate(calendar.timeInMillis)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            updateTasksForDate(calendar.timeInMillis)
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
        rvTasks.layoutManager = LinearLayoutManager(requireContext())
        rvTasks.adapter = adapter
    }

    private fun updateTasksForDate(timeInMillis: Long) {
        val dateString = sdf.format(timeInMillis)
        tvSelectedDate.text = "Tasks for $dateString"

        viewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            val filteredTasks = tasks.filter { it.task.deadline == dateString }
            adapter.setData(filteredTasks)
        }
    }
}
