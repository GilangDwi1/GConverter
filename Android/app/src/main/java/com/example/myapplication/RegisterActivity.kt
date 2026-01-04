package com.example.myapplication

import AuthRequest
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import api.settings.RetrofitInstance
import kotlinx.coroutines.launch

class RegisterActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity_layout)

        val etEmail = findViewById<EditText>(R.id.etEmailReg)
        val etPassword = findViewById<EditText>(R.id.etPasswordReg)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnToLogin = findViewById<TextView>(R.id.btnToLogin)

        btnRegister.setOnClickListener {
            val email = etEmail.text.toString()
            val pass = etPassword.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                lifecycleScope.launch {
                    try {
                        val response = RetrofitInstance.api.register(AuthRequest(email, pass))
                        if (response.isSuccessful) {
                            Toast.makeText(this@RegisterActivity, "Registrasi Berhasil!", Toast.LENGTH_SHORT).show()
                            finish() // Kembali ke Login
                        } else {
                            Toast.makeText(this@RegisterActivity, "Registrasi Gagal", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        btnToLogin.setOnClickListener { finish() }
    }
}
