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
            onDelete = ForeignKey.SET_DEFAULT // Or CASCADE
        )
    ],
    indices = [Index("categoryId")]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,
    val description: String,
    val deadline: String,
    val priority: String,
    val categoryId: Int, // Changed from category: String
    val isDone: Boolean = false
)
