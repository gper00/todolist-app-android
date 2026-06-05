package com.example.todolist.view.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.todolist.R
import com.example.todolist.view.LoginActivity
import com.example.todolist.view.ManageCategoriesActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        auth = FirebaseAuth.getInstance()

        val tvEmail = view.findViewById<TextView>(R.id.tvProfileEmail)
        val btnManageCategories = view.findViewById<MaterialButton>(R.id.btnManageCategories)
        val btnLogout = view.findViewById<MaterialButton>(R.id.btnLogout)

        tvEmail.text = auth.currentUser?.email ?: "No email"

        btnManageCategories.setOnClickListener {
            startActivity(Intent(requireContext(), ManageCategoriesActivity::class.java))
        }

        btnLogout.setOnClickListener {
            // Clear Remember Me preference
            val sharedPreferences = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean("isRemembered", false).apply()

            // Logout Firebase
            auth.signOut()

            // Go to Login
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        return view
    }
}
