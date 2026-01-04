package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import api.settings.RetrofitInstance
import kotlinx.coroutines.launch
import utils.FileUtil // Pastikan FileUtil sudah dibuat di package utils

class VideoActivity : ComponentActivity() {

    private lateinit var pickFileLauncher: ActivityResultLauncher<String>
    private lateinit var progressBar: ProgressBar
    private var currentMode: String = "" // Untuk menandai sedang MP4toMOV atau MOVtoMP4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_activity_layout)

        progressBar = findViewById(R.id.progressBar)
        val cardMP4toMOV = findViewById<LinearLayout>(R.id.cardMP4toMOV)
        val cardMOVtoMP4 = findViewById<LinearLayout>(R.id.cardMOVtoMP4)

        // Konfigurasi Picker
        pickFileLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                uri?.let {
                    Log.d("FILE_PICKER", "Dipilih: $it")
                    if (currentMode == "MP4_TO_MOV") {
                        uploadMp4(it)
                    } else {
                        uploadMov(it)
                    }
                }
            }

        cardMP4toMOV.setOnClickListener {
            currentMode = "MP4_TO_MOV"
            pickFileLauncher.launch("video/mp4")
        }

        cardMOVtoMP4.setOnClickListener {
            currentMode = "MOV_TO_MP4"
            pickFileLauncher.launch("video/quicktime") // Untuk file .mov
        }
    }

    // Convert MP4 to MOV
    private fun uploadMp4(uri: Uri) {
        performUpload(uri, "video/mp4")
    }

    // Convert MOV to MP4
    private fun uploadMov(uri: Uri) {
        performUpload(uri, "video/quicktime")
    }

    // Fungsi General untuk Upload agar kode tidak duplikat
    private fun performUpload(uri: Uri, mimeType: String) {
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE // Tampilkan loading

            // MENGGUNAKAN FILE UTIL (Otomatis deteksi ekstensi)
            val part = FileUtil.uriToMultipart(this@VideoActivity, uri)

            if (part != null) {
                try {
                    // Panggil API (Sesuaikan dengan fungsi API Anda)
                    val response = if (currentMode == "MP4_TO_MOV") {
                        RetrofitInstance.api.mp4ToMov(part)
                    } else {
                        RetrofitInstance.api.movToMp4(part) // Asumsi ada fungsi movToMp4 di API
                    }

                    if (response.isSuccessful && response.body() != null) {
                        val jobId = response.body()!!.job_id
                        val intent = Intent(this@VideoActivity, LoadingActivity::class.java)
                        intent.putExtra("JOB_ID", jobId)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@VideoActivity, "Upload gagal: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("UPLOAD_ERROR", e.message.toString())
                    Toast.makeText(this@VideoActivity, "Error Koneksi", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@VideoActivity, "Gagal memproses file", Toast.LENGTH_SHORT).show()
            }

            progressBar.visibility = View.GONE // Sembunyikan loading
        }
    }
}
