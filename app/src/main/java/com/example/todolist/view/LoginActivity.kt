package com.example.todolist.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var cbRememberMe: CheckBox
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)

        // Init views
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        cbRememberMe = findViewById(R.id.cbRememberMe)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)

        // AUTO-LOGIN CHECK
        val isRemembered = sharedPreferences.getBoolean("isRemembered", false)
        if (isRemembered && auth.currentUser != null) {
            goToMainActivity()
            return
        } else if (!isRemembered && auth.currentUser != null) {
            // User was logged in but didn't want to be remembered
            auth.signOut()
        }

        btnLogin.setOnClickListener {
            handleLogin()
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun handleLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Isi semua field", Toast.LENGTH_SHORT).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Email tidak valid"
            return
        }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Save Remember Me preference
                val editor = sharedPreferences.edit()
                editor.putBoolean("isRemembered", cbRememberMe.isChecked)
                editor.apply()

                Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show()
                goToMainActivity()
            } else {
                Toast.makeText(this, "Login gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
