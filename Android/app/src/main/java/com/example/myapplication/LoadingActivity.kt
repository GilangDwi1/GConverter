package com.example.myapplication
import android.os.Bundle
import android.widget.TextView
import android.widget.ProgressBar
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import api.settings.RetrofitInstance
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.content.Intent
import android.util.Log
import utils.FileUtil

class LoadingActivity : ComponentActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var txtStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_activity)

        progressBar = findViewById(R.id.progressBar)
        txtStatus = findViewById(R.id.txtStatus)

        val jobId = intent.getStringExtra("JOB_ID")

        if (jobId == null) {
            Log.e("LoadingActivity", "JOB_ID NULL")
            finish()
            return
        }

        startPolling(jobId)
    }

    private fun startPolling(jobId: String) {
        lifecycleScope.launch {

            while (true) {
                val response = RetrofitInstance.api.checkStatus(jobId)

                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!

                    progressBar.progress = data.progress
                    txtStatus.text = "Mengonversi... ${data.progress}%"

                    if (data.status == "done") {
                        downloadAndFinish(jobId)
                        break
                    }
                }

                delay(1500)
            }
        }
    }

    private suspend fun downloadAndFinish(jobId: String) {
        txtStatus.text = "Menyimpan file..."

        // Kita butuh objek 'response' lengkap, bukan hanya body-nya
        val response = RetrofitInstance.api.downloadFile(jobId)

        if (response.isSuccessful) {
            // MENGGUNAKAN FILE UTIL OTOMATIS
            val filePath = FileUtil.saveResponseToFile(this, response)

            if (filePath != null) {
                val intent = Intent(this, HistoryActivity::class.java)
                intent.putExtra("FILE_PATH", filePath)
                startActivity(intent)
                finish()
            } else {
                txtStatus.text = "Gagal menyimpan file"
            }
        } else {
            txtStatus.text = "Gagal mendownload"
        }
    }

// HAPUS FUNGSI saveFile LAMA ANDA

}

