package com.example.todolist.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.model.Category

class CategoryAdapter(
    private var categories: List<Category>,
    private val onEdit: (Category) -> Unit,
    private val onDelete: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val viewColor: View = itemView.findViewById(R.id.viewCategoryColor)
        val tvName: TextView = itemView.findViewById(R.id.tvCategoryName)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEditCategory)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.tvName.text = category.name
        
        // Set color circle
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.OVAL
        try {
            drawable.setColor(Color.parseColor(category.color))
        } catch (e: Exception) {
            drawable.setColor(Color.GRAY)
        }
        holder.viewColor.background = drawable

        holder.btnEdit.setOnClickListener { onEdit(category) }
        holder.btnDelete.setOnClickListener { onDelete(category) }
    }

    override fun getItemCount(): Int = categories.size

    fun setData(newList: List<Category>) {
        categories = newList
        notifyDataSetChanged()
    }
}
