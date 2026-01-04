package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import api.settings.RetrofitInstance
import kotlinx.coroutines.launch
import utils.FileUtil

class AudioActivity : ComponentActivity() {

    private lateinit var pickFileLauncher: ActivityResultLauncher<String>
    private var currentMode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.audio_activity_layout)

        // 1. Inisialisasi ID sesuai audio_activity_layout.xml
        val cardMP3toWAV = findViewById<LinearLayout>(R.id.cardMP3toWAV)
        val cardWAVtoMP3 = findViewById<LinearLayout>(R.id.cardWAVtoMP3)

        // 2. Konfigurasi Picker
        pickFileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                Log.d("AUDIO_PICKER", "Mode: $currentMode, File: $it")
                performUpload(it)
            }
        }

        // 3. Click Listeners
        cardMP3toWAV.setOnClickListener {
            currentMode = "MP3_TO_WAV"
            pickFileLauncher.launch("audio/mpeg") // Filter hanya file MP3
        }

        cardWAVtoMP3.setOnClickListener {
            currentMode = "WAV_TO_MP3"
            pickFileLauncher.launch("audio/x-wav") // Filter hanya file WAV
        }
    }

    private fun performUpload(uri: Uri) {
        lifecycleScope.launch {
            // Gunakan FileUtil untuk mengubah Uri menjadi MultipartBody.Part
            val part = FileUtil.uriToMultipart(this@AudioActivity, uri)

            if (part != null) {
                try {
                    // Panggil API berdasarkan mode yang dipilih
                    val response = when (currentMode) {
                        "MP3_TO_WAV" -> RetrofitInstance.api.mp3ToWav(part)
                        "WAV_TO_MP3" -> RetrofitInstance.api.wavToMp3(part)
                        else -> {
                            Log.e("UPLOAD_ERROR", "Mode tidak dikenal: $currentMode")
                            null
                        }
                    }

                    if (response != null && response.isSuccessful && response.body() != null) {
                        val jobId = response.body()!!.job_id

                        // Pindah ke LoadingActivity untuk memantau proses
                        val intent = Intent(this@AudioActivity, LoadingActivity::class.java)
                        intent.putExtra("JOB_ID", jobId)
                        startActivity(intent)
                    } else {
                        val errorMsg = response?.errorBody()?.string() ?: "Response null"
                        Log.e("UPLOAD_ERROR", "Gagal: $errorMsg")
                        Toast.makeText(this@AudioActivity, "Upload Gagal", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("UPLOAD_ERROR", "Exception: ${e.message}")
                    Toast.makeText(this@AudioActivity, "Error Koneksi", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@AudioActivity, "File tidak valid/tidak bisa dibaca", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
