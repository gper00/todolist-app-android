package com.example.todolist.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.todolist.model.Task
import com.example.todolist.model.TaskWithCategory

@Dao
interface TaskDao {

    @Insert
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Transaction
    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun getAllTasks(): LiveData<List<TaskWithCategory>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Int): TaskWithCategory // Changed to TaskWithCategory

    @Transaction
    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :search || '%'")
    fun searchTask(search: String): LiveData<List<TaskWithCategory>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE categoryId = :categoryId")
    fun getTasksByCategory(categoryId: Int): LiveData<List<TaskWithCategory>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE priority = :priority")
    fun getTasksByPriority(priority: String): LiveData<List<TaskWithCategory>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE categoryId = :categoryId AND priority = :priority")
    fun getTasksByCategoryAndPriority(categoryId: Int, priority: String): LiveData<List<TaskWithCategory>>

    @Query("DELETE FROM tasks WHERE isDone = 1")
    suspend fun deleteAllCompletedTasks()
}
