package com.example.todolist.repository

import androidx.lifecycle.LiveData
import com.example.todolist.database.TaskDao
import com.example.todolist.model.Task

class TodoRepository(private val taskDao: TaskDao) {
    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()

    suspend fun insert(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun delete(task: Task) {
        taskDao.deleteTask(task)
    }
}
