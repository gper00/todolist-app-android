package com.example.todolist.view.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.todolist.R
import com.example.todolist.view.LoginActivity
import com.example.todolist.viewmodel.TaskViewModel
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private val viewModel: TaskViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        auth = FirebaseAuth.getInstance()

        val tvEmail = view.findViewById<TextView>(R.id.tvProfileEmail)
        val tvTotalTasks = view.findViewById<TextView>(R.id.tvTotalTasks)
        val tvCompletedTasks = view.findViewById<TextView>(R.id.tvCompletedTasks)
        val btnLogout = view.findViewById<MaterialButton>(R.id.btnLogout)

        tvEmail.text = auth.currentUser?.email ?: "No email"

        // Observe tasks for stats
        viewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            tvTotalTasks.text = tasks.size.toString()
            val completedCount = tasks.count { it.task.isDone }
            tvCompletedTasks.text = completedCount.toString()
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
