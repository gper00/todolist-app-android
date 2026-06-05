package com.example.todolist.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.todolist.model.User

@Dao
interface UserDao {

    @Insert
    suspend fun register(user: User)

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    suspend fun login(
        email: String,
        password: String
    ): User?
}
