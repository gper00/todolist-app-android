package com.example.todolist.view.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarFragment : Fragment() {

    private lateinit var calendarView: MaterialCalendarView
    private lateinit var rvTasks: RecyclerView
    private lateinit var adapter: TaskAdapter
    private lateinit var tvSelectedDate: TextView
    private lateinit var btnAddTask: FloatingActionButton

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
        btnAddTask = view.findViewById(R.id.btnAddTaskCalendar)

        setupRecyclerView()

        // Set current date
        val now = CalendarDay.today()
        calendarView.setSelectedDate(now)
        updateTasksForDate(now)

        calendarView.setOnDateChangedListener { _, date, _ ->
            updateTasksForDate(date)
        }
        
        btnAddTask.setOnClickListener {
            startActivity(Intent(requireContext(), AddTaskActivity::class.java))
        }

        setupObservers()

        return view
    }

    private fun setupRecyclerView() {
        adapter = TaskAdapter(
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
        rvTasks.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            // Update decorators
            val datesWithTasks = mutableSetOf<CalendarDay>()
            tasks.forEach {
                try {
                    val date = sdf.parse(it.task.deadline)
                    if (date != null) {
                        val cal = Calendar.getInstance()
                        cal.time = date
                        datesWithTasks.add(CalendarDay.from(cal))
                    }
                } catch (e: Exception) {}
            }
            
            calendarView.removeDecorators()
            calendarView.addDecorator(EventDecorator(Color.RED, datesWithTasks))
            
            // Refresh current selected date list
            calendarView.selectedDate?.let { updateTasksForDate(it) }
        }
    }

    private fun updateTasksForDate(date: CalendarDay) {
        val dateString = sdf.format(date.date) // CalendarDay.getDate() returns java.util.Date in 1.4.3
        tvSelectedDate.text = "Tasks for $dateString"

        viewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            val filteredTasks = tasks.filter { it.task.deadline == dateString }
            adapter.setData(filteredTasks)
        }
    }

    // Decorator class for marking dates
    class EventDecorator(private val color: Int, private val dates: Collection<CalendarDay>) : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return dates.contains(day)
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(DotSpan(5f, color))
        }
    }
}
