package com.example.todolist.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL // Changed from SET_DEFAULT/CASCADE
        )
    ],
    indices = [Index("categoryId")]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,
    val description: String = "", // Added default value
    val deadline: String = "",    // Added default value
    val priority: String = "",    // Added default value
    val categoryId: Int? = null,  // Changed to nullable Int?
    val isDone: Boolean = false
)
