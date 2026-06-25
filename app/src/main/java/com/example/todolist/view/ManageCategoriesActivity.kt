package com.example.todolist.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.adapter.CategoryAdapter
import com.example.todolist.model.Category
import com.example.todolist.utils.ThemeStorage
import com.example.todolist.viewmodel.TaskViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ManageCategoriesActivity : AppCompatActivity() {

    private lateinit var rvCategories: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: CategoryAdapter
    private lateinit var themeStorage: ThemeStorage
    private val viewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Terapkan tema
        themeStorage = ThemeStorage(this)
        setTheme(themeStorage.getThemeResource())

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_categories)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        rvCategories = findViewById(R.id.rvCategories)
        tvEmpty = findViewById(R.id.tvEmptyCategories)
        val btnAdd: FloatingActionButton = findViewById(R.id.btnAddCategory)

        setupRecyclerView()
        setupObservers()

        btnAdd.setOnClickListener {
            showCategoryDialog(null)
        }
    }

    private fun setupRecyclerView() {
        adapter = CategoryAdapter(
            emptyList(),
            onEdit = { category -> showCategoryDialog(category) },
            onDelete = { category ->
                AlertDialog.Builder(this)
                    .setTitle("Delete Category")
                    .setMessage("Are you sure you want to delete '${category.name}'? Tasks in this category will not be deleted.")
                    .setPositiveButton("Delete") { _, _ ->
                        viewModel.deleteCategory(category)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )
        rvCategories.layoutManager = LinearLayoutManager(this)
        rvCategories.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.allCategories.observe(this) { categories ->
            adapter.setData(categories)
            tvEmpty.visibility = if (categories.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun showCategoryDialog(category: Category?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null)
        val etName = dialogView.findViewById<EditText>(R.id.etCategoryName)
        val colorGrid = dialogView.findViewById<android.widget.GridLayout>(R.id.colorGrid)
        
        var selectedColor = category?.color ?: "#6C63FF"

        val colors = listOf(
            "#FF5252", "#FF4081", "#7C4DFF", "#9C27B0", "#3F51B5", "#2196F3",
            "#00BCD4", "#009688", "#4CAF50", "#FF9800", "#795548", "#9E9E9E",
            "#6C63FF", "#E91E63", "#000000", "#FFC107", "#8BC34A", "#607D8B"
        )

        colors.forEach { colorStr ->
            val view = View(this)
            val params = android.widget.GridLayout.LayoutParams()
            params.width = 100
            params.height = 100
            params.setMargins(10, 10, 10, 10)
            view.layoutParams = params
            
            val drawable = android.graphics.drawable.GradientDrawable()
            drawable.shape = android.graphics.drawable.GradientDrawable.OVAL
            drawable.setColor(android.graphics.Color.parseColor(colorStr))
            
            // Highlight selected
            if (colorStr == selectedColor) {
                drawable.setStroke(6, android.graphics.Color.DKGRAY)
            } else {
                drawable.setStroke(0, android.graphics.Color.TRANSPARENT)
            }
            
            view.background = drawable
            view.setOnClickListener {
                selectedColor = colorStr
                // Refresh highlight
                for (i in 0 until colorGrid.childCount) {
                    val child = colorGrid.getChildAt(i)
                    val childDrawable = child.background as android.graphics.drawable.GradientDrawable
                    if (colors[i] == selectedColor) {
                        childDrawable.setStroke(6, android.graphics.Color.DKGRAY)
                    } else {
                        childDrawable.setStroke(0, android.graphics.Color.TRANSPARENT)
                    }
                }
            }
            colorGrid.addView(view)
        }

        if (category != null) {
            etName.setText(category.name)
        }

        AlertDialog.Builder(this)
            .setTitle(if (category == null) "Add Category" else "Edit Category")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = etName.text.toString().trim()

                if (name.isNotEmpty()) {
                    if (category == null) {
                        viewModel.insertCategory(Category(name = name, color = selectedColor))
                    } else {
                        viewModel.updateCategory(category.copy(name = name, color = selectedColor))
                    }
                } else {
                    Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
