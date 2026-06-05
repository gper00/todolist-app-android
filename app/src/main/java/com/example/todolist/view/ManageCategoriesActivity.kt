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
import com.example.todolist.viewmodel.TaskViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ManageCategoriesActivity : AppCompatActivity() {

    private lateinit var rvCategories: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: CategoryAdapter
    private val viewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
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
            onDelete = { category -> viewModel.deleteCategory(category) }
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
        val etColor = dialogView.findViewById<EditText>(R.id.etCategoryColor)

        if (category != null) {
            etName.setText(category.name)
            etColor.setText(category.color)
        } else {
            etColor.setText("#6C63FF") // Default color
        }

        AlertDialog.Builder(this)
            .setTitle(if (category == null) "Add Category" else "Edit Category")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = etName.text.toString().trim()
                val color = etColor.text.toString().trim()

                if (name.isNotEmpty() && color.isNotEmpty()) {
                    if (category == null) {
                        viewModel.insertCategory(Category(name = name, color = color))
                    } else {
                        viewModel.updateCategory(category.copy(name = name, color = color))
                    }
                } else {
                    Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
