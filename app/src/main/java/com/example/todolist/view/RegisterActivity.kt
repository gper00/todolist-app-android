package com.example.todolist.view

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.R
import com.google.firebase.auth.FirebaseAuth
import android.widget.TextView

class RegisterActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText

    private lateinit var btnRegister: Button

    private lateinit var btnBackLogin: TextView

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)

        btnRegister = findViewById(R.id.btnRegister)
        btnBackLogin = findViewById(R.id.btnBackLogin)

        btnRegister.setOnClickListener {

            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Validasi kosong
            if (
                username.isEmpty() ||
                email.isEmpty() ||
                password.isEmpty()
            ) {

                Toast.makeText(
                    this,
                    "Semua field wajib diisi",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // Validasi email
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                etEmail.error = "Format email tidak valid"
                etEmail.requestFocus()

                return@setOnClickListener
            }

            // Password minimal
            if (password.length < 6) {

                etPassword.error = "Password minimal 6 karakter"
                etPassword.requestFocus()

                return@setOnClickListener
            }

            // Register Firebase
            auth.createUserWithEmailAndPassword(
                email,
                password
            ).addOnCompleteListener {

                if (it.isSuccessful) {

                    Toast.makeText(
                        this,
                        "Register berhasil",
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(
                        Intent(
                            this,
                            LoginActivity::class.java
                        )
                    )

                    finish()

                } else {

                    Toast.makeText(
                        this,
                        it.exception?.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        btnBackLogin.setOnClickListener {
            finish()
        }
    }
}