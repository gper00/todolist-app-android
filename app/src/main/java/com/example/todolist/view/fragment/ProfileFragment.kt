package com.example.todolist.view.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.todolist.R
import com.example.todolist.utils.ThemeStorage
import com.example.todolist.view.LoginActivity
import com.example.todolist.view.MainActivity
import com.example.todolist.viewmodel.TaskViewModel
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private val viewModel: TaskViewModel by viewModels()
    private lateinit var themeStorage: ThemeStorage
    
    private lateinit var ivProfileImage: ImageView
    private lateinit var tvProfileName: TextView
    private lateinit var tvProfileEmail: TextView
    private var selectedImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            ivProfileImage.setImageURI(uri)
            updateProfile(tvProfileName.text.toString(), uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        auth = FirebaseAuth.getInstance()
        themeStorage = ThemeStorage(requireContext())

        ivProfileImage = view.findViewById(R.id.ivProfileImage)
        tvProfileName = view.findViewById(R.id.tvProfileName)
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail)
        val tvTotalTasks = view.findViewById<TextView>(R.id.tvTotalTasks)
        val tvCompletedTasks = view.findViewById<TextView>(R.id.tvCompletedTasks)
        val btnLogout = view.findViewById<MaterialButton>(R.id.btnLogout)
        val btnEditProfile = view.findViewById<MaterialButton>(R.id.btnEditProfile)

        // Theme Pickers
        view.findViewById<View>(R.id.viewThemePurple).setOnClickListener { changeTheme("purple") }
        view.findViewById<View>(R.id.viewThemeBlue).setOnClickListener { changeTheme("blue") }
        view.findViewById<View>(R.id.viewThemeGreen).setOnClickListener { changeTheme("green") }
        view.findViewById<View>(R.id.viewThemePink).setOnClickListener { changeTheme("pink") }

        loadUserData()

        viewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            tvTotalTasks.text = tasks.size.toString()
            val completedCount = tasks.count { it.task.isDone }
            tvCompletedTasks.text = completedCount.toString()
        }

        btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        btnLogout.setOnClickListener {
            val sharedPreferences = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean("isRemembered", false).apply()
            auth.signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        return view
    }

    private fun loadUserData() {
        val user = auth.currentUser
        tvProfileName.text = user?.displayName ?: "User Name"
        tvProfileEmail.text = user?.email ?: "No email"
        if (user?.photoUrl != null) {
            ivProfileImage.setImageURI(user.photoUrl)
        }
    }

    private fun changeTheme(themeName: String) {
        if (themeStorage.getTheme() != themeName) {
            themeStorage.setTheme(themeName)
            (activity as? MainActivity)?.restartActivity()
        }
    }

    private fun showEditProfileDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Edit Profile")

        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        val etName = dialogView.findViewById<EditText>(R.id.etEditName)
        val etPassword = dialogView.findViewById<EditText>(R.id.etEditPassword)
        val etConfirmPassword = dialogView.findViewById<EditText>(R.id.etConfirmPassword)
        val btnChangePhoto = dialogView.findViewById<MaterialButton>(R.id.btnChangePhoto)
        
        etName.setText(tvProfileName.text)

        btnChangePhoto.setOnClickListener {
            pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        builder.setView(dialogView)
        builder.setPositiveButton("Simpan") { _, _ ->
            val newName = etName.text.toString().trim()
            val newPassword = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            if (newName.isNotEmpty()) {
                updateProfile(newName, selectedImageUri)
            }

            if (newPassword.isNotEmpty()) {
                if (newPassword.length < 6) {
                    Toast.makeText(context, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                } else if (newPassword != confirmPassword) {
                    Toast.makeText(context, "Konfirmasi password tidak cocok", Toast.LENGTH_SHORT).show()
                } else {
                    updatePassword(newPassword)
                }
            }
        }
        builder.setNegativeButton("Batal", null)
        builder.show()
    }

    private fun updateProfile(name: String, photoUri: Uri?) {
        val user = auth.currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .apply {
                if (photoUri != null) setPhotoUri(photoUri)
            }
            .build()

        user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                tvProfileName.text = name
                Toast.makeText(context, "Profil diperbarui", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Gagal memperbarui profil", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updatePassword(password: String) {
        val user = auth.currentUser
        user?.updatePassword(password)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Password berhasil diubah", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Gagal mengubah password: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
