package com.example.todolist.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.model.Category

class CategoryBadgeAdapter(
    private var categories: List<Category>,
    private val onClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryBadgeAdapter.BadgeViewHolder>() {

    private var selectedCategoryId: Int? = null

    class BadgeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvBadge: TextView = itemView.findViewById(R.id.tvBadgeName)
        val cardBadge: com.google.android.material.card.MaterialCardView = itemView.findViewById(R.id.cardBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_badge, parent, false)
        return BadgeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        val category = categories[position]
        holder.tvBadge.text = category.name
        try {
            val color = Color.parseColor(category.color)
            holder.cardBadge.setCardBackgroundColor(color)

            // Dynamic text color for contrast
            val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
            holder.tvBadge.setTextColor(if (darkness < 0.5) Color.BLACK else Color.WHITE)

            // Highlight selected category with stroke
            if (selectedCategoryId == category.id) {
                holder.cardBadge.strokeWidth = 4
                holder.cardBadge.strokeColor = Color.BLACK
            } else {
                holder.cardBadge.strokeWidth = 0
            }
        } catch (e: Exception) {
            holder.cardBadge.setCardBackgroundColor(Color.GRAY)
            holder.tvBadge.setTextColor(Color.WHITE)
            if (selectedCategoryId == category.id) {
                holder.cardBadge.strokeWidth = 4
                holder.cardBadge.strokeColor = Color.BLACK
            } else {
                holder.cardBadge.strokeWidth = 0
            }
        }

        holder.itemView.setOnClickListener { onClick(category) }
    }

    override fun getItemCount(): Int = categories.size

    fun setData(newList: List<Category>) {
        categories = newList
        notifyDataSetChanged()
    }

    fun setSelectedCategory(categoryId: Int?) {
        selectedCategoryId = categoryId
        notifyDataSetChanged()
    }
}
