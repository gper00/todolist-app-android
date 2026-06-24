package com.example.todolist.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.todolist.R
import com.example.todolist.utils.ThemeStorage
import com.example.todolist.view.fragment.CalendarFragment
import com.example.todolist.view.fragment.HomeFragment
import com.example.todolist.view.fragment.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var themeStorage: ThemeStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        themeStorage = ThemeStorage(this)
        setTheme(themeStorage.getThemeResource())
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.bottomNavigation)

        // Set default fragment
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.nav_calendar -> {
                    replaceFragment(CalendarFragment())
                    true
                }
                R.id.nav_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
    
    fun restartActivity() {
        finish()
        startActivity(intent)
    }
}
