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

class FileActivity : ComponentActivity() {

    private lateinit var pickFileLauncher: ActivityResultLauncher<String>
    // Opsional: Jika Anda menambahkan ProgressBar di file_activity_layout.xml
    // private lateinit var progressBar: ProgressBar

    private var currentMode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.file_activity_layout)

        // Inisialisasi ID sesuai file_activity_layout.xml
        val cardPdftoWord = findViewById<LinearLayout>(R.id.cardPdftoWord)
        val cardXLSXtoPDF = findViewById<LinearLayout>(R.id.cardXLSXtoPDF)
        val cardWordtoPDF = findViewById<LinearLayout>(R.id.cardWordtoPDF)

        // Konfigurasi Picker
        pickFileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                Log.d("FILE_PICKER", "Mode: $currentMode, File: $it")
                performUpload(it)
            }
        }

        // Click Listeners
        cardPdftoWord.setOnClickListener {
            currentMode = "PDF_TO_WORD"
            pickFileLauncher.launch("application/pdf")
        }

        cardXLSXtoPDF.setOnClickListener {
            currentMode = "XLSX_TO_PDF"
            pickFileLauncher.launch("application/vnd.ms-excel")
        }

        cardWordtoPDF.setOnClickListener {
            currentMode = "WORD_TO_PDF"
            // Picker untuk file Word (.doc atau .docx)
            pickFileLauncher.launch("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
        }
    }

    private fun performUpload(uri: Uri) {
        lifecycleScope.launch {
            val part = FileUtil.uriToMultipart(this@FileActivity, uri)

            if (part != null) {
                try {
                    // SESUAIKAN MODE DENGAN BENAR
                    val response = when (currentMode) {
                        "PDF_TO_WORD" -> RetrofitInstance.api.pdfToWord(part)
                        "XLSX_TO_PDF" -> RetrofitInstance.api.xlsxToPdf(part) // Pastikan ini XLSX_TO_PDF
                        "WORD_TO_PDF" -> RetrofitInstance.api.wordToPdf(part)
                        else -> {
                            Log.e("UPLOAD_ERROR", "Mode tidak dikenal: $currentMode")
                            null
                        }
                    }

                    if (response != null && response.isSuccessful && response.body() != null) {
                        val jobId = response.body()!!.job_id
                        val intent = Intent(this@FileActivity, LoadingActivity::class.java)
                        intent.putExtra("JOB_ID", jobId)
                        startActivity(intent)
                    } else {
                        val errorMsg = response?.errorBody()?.string() ?: "Response null"
                        Log.e("UPLOAD_ERROR", "Gagal: $errorMsg")
                        Toast.makeText(this@FileActivity, "Upload Gagal", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("UPLOAD_ERROR", "Exception: ${e.message}")
                    Toast.makeText(this@FileActivity, "Error Koneksi", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@FileActivity, "File tidak valid/tidak bisa dibaca", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
