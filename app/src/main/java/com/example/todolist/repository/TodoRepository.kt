package com.example.todolist.repository

import androidx.lifecycle.LiveData
import com.example.todolist.database.CategoryDao
import com.example.todolist.database.TaskDao
import com.example.todolist.database.UserDao
import com.example.todolist.model.Category
import com.example.todolist.model.Task
import com.example.todolist.model.TaskWithCategory
import com.example.todolist.model.User

class TodoRepository(
    private val taskDao: TaskDao,
    private val categoryDao: CategoryDao,
    private val userDao: UserDao
) {
    // TASKS
    val allTasks: LiveData<List<TaskWithCategory>> = taskDao.getAllTasks()

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    fun searchTasks(query: String): LiveData<List<TaskWithCategory>> {
        return taskDao.searchTask(query)
    }

    fun getTasksByCategory(categoryId: Int): LiveData<List<TaskWithCategory>> {
        return taskDao.getTasksByCategory(categoryId)
    }

    fun getTasksByPriority(priority: String): LiveData<List<TaskWithCategory>> {
        return taskDao.getTasksByPriority(priority)
    }

    fun getTasksByCategoryAndPriority(categoryId: Int, priority: String): LiveData<List<TaskWithCategory>> {
        return taskDao.getTasksByCategoryAndPriority(categoryId, priority)
    }

    suspend fun deleteAllCompletedTasks() {
        taskDao.deleteAllCompletedTasks()
    }

    // CATEGORIES
    val allCategories: LiveData<List<Category>> = categoryDao.getAllCategories()

    suspend fun insertCategory(category: Category) {
        categoryDao.insertCategory(category)
    }

    suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category)
    }

    suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category)
    }

    // USER
    suspend fun registerUser(user: User) {
        userDao.register(user)
    }

    suspend fun loginUser(email: String, password: String): User? {
        return userDao.login(email, password)
    }
}
