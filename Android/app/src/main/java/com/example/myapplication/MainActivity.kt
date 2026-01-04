package com.example.myapplication

import android.os.Bundle
import android.content.Intent
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {

    // PERBAIKAN 1: Registrasi launcher dipindah ke luar onCreate agar stabil
    private val scannerLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val scanResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
            scanResult?.pdf?.let { pdf ->
                savePdfToInternalStorage(pdf.uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout)

        // Inisialisasi UI
        val cardVideo = findViewById<LinearLayout>(R.id.cardVideo)
        val cardImage = findViewById<LinearLayout>(R.id.cardImage)
        val cardFile = findViewById<LinearLayout>(R.id.cardFile)
        val cardAudio = findViewById<LinearLayout>(R.id.cardAudio)
        val cardHistory = findViewById<LinearLayout>(R.id.cardHistory)
        val cardUser = findViewById<LinearLayout>(R.id.cardUser)
        val floatingScan = findViewById<ImageView>(R.id.floatingScan)

        // Navigasi Card
        cardVideo.setOnClickListener { startActivity(Intent(this, VideoActivity::class.java)) }
        cardImage.setOnClickListener { startActivity(Intent(this, ImageActivity::class.java)) }
        cardFile.setOnClickListener { startActivity(Intent(this, FileActivity::class.java)) }
        cardAudio.setOnClickListener { startActivity(Intent(this, AudioActivity::class.java)) }
        cardHistory.setOnClickListener { startActivity(Intent(this, HistoryActivity::class.java)) }
        cardUser.setOnClickListener { startActivity(Intent(this, LoginActivity::class.java)) }

        // PERBAIKAN 2: Konfigurasi scanner dipisah agar lebih bersih
        floatingScan.setOnClickListener {
            startDocumentScan()
        }
    }

    private fun startDocumentScan() {
        val options = GmsDocumentScannerOptions.Builder()
            .setGalleryImportAllowed(true)
            .setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_PDF)
            .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
            .build()

        val scanner = GmsDocumentScanning.getClient(options)

        scanner.getStartScanIntent(this)
            .addOnSuccessListener { intentSender ->
                // Memanggil launcher dengan cara yang benar
                scannerLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun savePdfToInternalStorage(uri: android.net.Uri) {
        try {
            // PERBAIKAN 3: Menggunakan .use agar stream otomatis tertutup (mencegah memory leak/file corrupt)
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val fileName = "Scan_${System.currentTimeMillis()}.pdf"
                val outputFile = File(getExternalFilesDir(null), fileName)

                FileOutputStream(outputFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            Toast.makeText(this, "Scan berhasil disimpan!", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Gagal menyimpan file", Toast.LENGTH_SHORT).show()
        }
    }
}
