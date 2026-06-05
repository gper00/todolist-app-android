package com.example.todolist.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.todolist.database.AppDatabase
import com.example.todolist.model.Task
import com.example.todolist.repository.TodoRepository
import kotlinx.coroutines.launch

class TaskViewModel(application: Application)
    : AndroidViewModel(application) {

    private val repository: TodoRepository
    val allTasks: LiveData<List<Task>>

    init {
        val dao = AppDatabase
            .getDatabase(application)
            .taskDao()

        repository = TodoRepository(dao)
        allTasks = repository.allTasks
    }

    fun insert(task: Task) =
        viewModelScope.launch {
            repository.insert(task)
        }

    fun delete(task: Task) =
        viewModelScope.launch {
            repository.delete(task)
        }
}
