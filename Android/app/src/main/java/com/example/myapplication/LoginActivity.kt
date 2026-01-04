package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import AuthRequest
import api.settings.RetrofitInstance
import kotlinx.coroutines.launch
import utils.SessionManager

class LoginActivity : ComponentActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi Session Manager
        sessionManager = SessionManager(this)

        // Cek jika sudah login, langsung ke MainActivity
        if (sessionManager.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        setContentView(R.layout.login_activity_layout)

        // Inisialisasi UI
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnToRegister = findViewById<TextView>(R.id.btnToRegister)

        // Aksi Tombol Login
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            performLogin(email, password)
        }

        // Navigasi ke Halaman Register
        btnToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun performLogin(email: String, password: String) {
        lifecycleScope.launch {
            try {
                val request = AuthRequest(email, password)
                val response = RetrofitInstance.api.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!

                    // Simpan token dan status premium ke session
                    sessionManager.saveAuthToken(authResponse.access_token)
                    sessionManager.setPremiumStatus(authResponse.is_premium)

                    Toast.makeText(this@LoginActivity, "Login Berhasil!", Toast.LENGTH_SHORT).show()

                    // Pindah ke MainActivity
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@LoginActivity, "Login Gagal: Email atau Password salah", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Koneksi Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
