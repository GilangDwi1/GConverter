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
import utils.FileUtil

class ImageActivity : ComponentActivity() {

    private lateinit var pickFileLauncher: ActivityResultLauncher<String>
    // Opsional: Tambahkan ProgressBar di XML jika ingin menampilkan loading
    // private lateinit var progressBar: ProgressBar

    private var currentMode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_activity_layout)

        // Inisialisasi ID sesuai XML
        val cardJPGtoPNG = findViewById<LinearLayout>(R.id.cardJPGtoPNG)
        val cardPNGtoJPG = findViewById<LinearLayout>(R.id.cardPNGtoJPG)
        val cardJPGtoWebp = findViewById<LinearLayout>(R.id.cardJPGtoWebp)
        val cardPNGtoWebp = findViewById<LinearLayout>(R.id.cardPNGtoWebp)
        val cardWebptoPNG = findViewById<LinearLayout>(R.id.cardWebptoPNG)
        val cardWebptoJPG = findViewById<LinearLayout>(R.id.cardWebptoJPG)



        // Konfigurasi Picker
        pickFileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                Log.d("IMAGE_PICKER", "Mode: $currentMode, File: $it")
                performUpload(it)
            }
        }

        // Click Listeners
        cardJPGtoPNG.setOnClickListener {
            currentMode = "JPG_TO_PNG"
            pickFileLauncher.launch("image/jpeg")
        }

        cardPNGtoJPG.setOnClickListener {
            currentMode = "PNG_TO_JPG"
            pickFileLauncher.launch("image/png")
        }

        cardJPGtoWebp.setOnClickListener {
            currentMode = "JPG_TO_WEBP"
            pickFileLauncher.launch("image/jpeg")
        }

        cardPNGtoWebp.setOnClickListener {
            currentMode = "PNG_TO_WEBP"
            pickFileLauncher.launch("image/png")
        }

        cardWebptoPNG.setOnClickListener {
            currentMode = "WEBP_TO_PNG"
            pickFileLauncher.launch("image/webp")
        }

        cardWebptoJPG.setOnClickListener {
            currentMode = "WEBP_TO_JPG"
            pickFileLauncher.launch("image/webp")
        }
    }

    private fun performUpload(uri: Uri) {
        lifecycleScope.launch {
            // Tampilkan loading jika ada
            // progressBar.visibility = View.VISIBLE

            val part = FileUtil.uriToMultipart(this@ImageActivity, uri)

            if (part != null) {
                try {
                    // Sesuaikan pemanggilan API berdasarkan currentMode
                    val response = when (currentMode) {
                        "JPG_TO_PNG" -> RetrofitInstance.api.jpgToPng(part)
                        "PNG_TO_JPG" -> RetrofitInstance.api.pngToJpg(part)
                        "JPG_TO_WEBP" -> RetrofitInstance.api.jpgToWebp(part)
                        "PNG_TO_WEBP" -> RetrofitInstance.api.pngToWebp(part)
                        "WEBP_TO_PNG" -> RetrofitInstance.api.webpToPng(part)
                        "WEBP_TO_JPG" -> RetrofitInstance.api.webpToJpg(part)
                        else -> null
                    }

                    if (response != null && response.isSuccessful && response.body() != null) {
                        val jobId = response.body()!!.job_id

                        val intent = Intent(this@ImageActivity, LoadingActivity::class.java)
                        intent.putExtra("JOB_ID", jobId)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@ImageActivity, "Upload Gagal", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("UPLOAD_ERROR", e.message.toString())
                    Toast.makeText(this@ImageActivity, "Error Koneksi", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@ImageActivity, "File tidak valid", Toast.LENGTH_SHORT).show()
            }

            // progressBar.visibility = View.GONE
        }
    }
}
