package com.example.todolist.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.todolist.R

class ThemeStorage(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

    fun setTheme(theme: String) {
        prefs.edit().putString("selected_theme", theme).apply()
    }

    fun getTheme(): String {
        return prefs.getString("selected_theme", "purple") ?: "purple"
    }

    fun getThemeResource(): Int {
        return when (getTheme()) {
            "blue" -> R.style.Theme_ToDoList_Blue
            "green" -> R.style.Theme_ToDoList_Green
            "pink" -> R.style.Theme_ToDoList_Pink
            else -> R.style.Theme_ToDoList
        }
    }
}
