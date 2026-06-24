package com.example.todolist.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.todolist.database.AppDatabase
import com.example.todolist.model.Category
import com.example.todolist.model.Task
import com.example.todolist.model.TaskWithCategory
import com.example.todolist.repository.TodoRepository
import kotlinx.coroutines.launch

class TaskViewModel(application: Application)
    : AndroidViewModel(application) {

    private val repository: TodoRepository
    val allTasks: LiveData<List<TaskWithCategory>>
    val allCategories: LiveData<List<Category>>

    init {
        val db = AppDatabase.getDatabase(application)
        val taskDao = db.taskDao()
        val categoryDao = db.categoryDao()
        val userDao = db.userDao()

        repository = TodoRepository(taskDao, categoryDao, userDao)
        allTasks = repository.allTasks
        allCategories = repository.allCategories
    }

    // TASKS
    fun insertTask(task: Task) = viewModelScope.launch {
        repository.insertTask(task)
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        repository.updateTask(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        repository.deleteTask(task)
    }

    fun searchTasks(query: String): LiveData<List<TaskWithCategory>> {
        return repository.searchTasks(query)
    }

    fun getTasksByCategory(categoryId: Int): LiveData<List<TaskWithCategory>> {
        return repository.getTasksByCategory(categoryId)
    }

    fun getTasksByPriority(priority: String): LiveData<List<TaskWithCategory>> {
        return repository.getTasksByPriority(priority)
    }

    fun getTasksByCategoryAndPriority(categoryId: Int, priority: String): LiveData<List<TaskWithCategory>> {
        return repository.getTasksByCategoryAndPriority(categoryId, priority)
    }

    fun deleteAllCompletedTasks() = viewModelScope.launch {
        repository.deleteAllCompletedTasks()
    }

    // CATEGORIES
    fun insertCategory(category: Category) = viewModelScope.launch {
        repository.insertCategory(category)
    }

    fun updateCategory(category: Category) = viewModelScope.launch {
        repository.updateCategory(category)
    }

    fun deleteCategory(category: Category) = viewModelScope.launch {
        repository.deleteCategory(category)
    }
}
