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

    class BadgeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvBadge: TextView = itemView.findViewById(R.id.tvBadgeName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_badge, parent, false)
        return BadgeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        val category = categories[position]
        holder.tvBadge.text = category.name
        try {
            holder.tvBadge.setTextColor(Color.parseColor(category.color))
        } catch (e: Exception) {
            holder.tvBadge.setTextColor(Color.BLACK)
        }
        
        holder.itemView.setOnClickListener { onClick(category) }
    }

    override fun getItemCount(): Int = categories.size

    fun setData(newList: List<Category>) {
        categories = newList
        notifyDataSetChanged()
    }
}
