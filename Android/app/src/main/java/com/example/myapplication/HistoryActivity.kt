package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import utils.HistoryItem
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class HistoryActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history_activity_layout)

        val rvHistory = findViewById<RecyclerView>(R.id.rvHistory)
        rvHistory.layoutManager = LinearLayoutManager(this)

        val historyList = loadHistory()

        if (historyList.isEmpty()) {
            Toast.makeText(this, "Belum ada file hasil konversi", Toast.LENGTH_SHORT).show()
        }

        rvHistory.adapter = HistoryAdapter(historyList) { file ->
            openFile(file)
        }
    }

    private fun loadHistory(): List<HistoryItem> {
        val historyItems = mutableListOf<HistoryItem>()
        // Folder ini HARUS SAMA dengan tempat FileUtil menyimpan file (getExternalFilesDir)
        val directory = getExternalFilesDir(null)

        val files = directory?.listFiles()
        files?.sortByDescending { it.lastModified() } // File terbaru di atas

        files?.forEach { file ->
            if (file.isFile) {
                val date = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                    .format(Date(file.lastModified()))

                val size = String.format("%.2f MB", file.length().toDouble() / (1024 * 1024))

                historyItems.add(
                    HistoryItem(file.name, file.absolutePath, size, date, file)
                )
            }
        }
        return historyItems
    }

    private fun openFile(file: java.io.File) {
        try {
            // 1. Buat URI menggunakan FileProvider
            val uri: android.net.Uri = androidx.core.content.FileProvider.getUriForFile(
                this,
                "${packageName}.provider",
                file
            )

            // 2. Ambil tipe MIME file (misal: application/pdf, image/png)
            val contentResolver = contentResolver
            val mimeType = contentResolver.getType(uri)

            // 3. Buat Intent untuk membuka file
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
            intent.setDataAndType(uri, mimeType)

            // 4. Berikan izin baca kepada aplikasi yang akan membuka file
            intent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)

            // 5. Jalankan Intent
            startActivity(intent)
        } catch (e: Exception) {
            android.util.Log.e("HISTORY_ERROR", "Gagal membuka file: ${e.message}")
            android.widget.Toast.makeText(this, "Tidak ada aplikasi yang mendukung file ini", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

}
