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
}
