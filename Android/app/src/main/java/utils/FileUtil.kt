package utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
object FileUtil {
    fun uriToMultipart(context: Context, uri: Uri, partName: String = "file"): MultipartBody.Part? {
        try {
            val contentResolver = context.contentResolver

            // 1. Dapatkan nama file asli dan ekstensi
            var fileName = "temp_file"
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst()) {
                    fileName = cursor.getString(nameIndex)
                }
            }

            // 2. Dapatkan tipe MIME (misal: video/mp4, image/jpeg)
            val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"

            // 3. Buat file temporary di cache
            val file = File(context.cacheDir, fileName)
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val outputStream = FileOutputStream(file)

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            // 4. Buat RequestBody dengan tipe MIME otomatis
            val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())

            // 5. Kembalikan sebagai MultipartBody.Part
            return MultipartBody.Part.createFormData(partName, file.name, requestBody)

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }


    fun saveResponseToFile(
        context: Context,
        response: retrofit2.Response<okhttp3.ResponseBody>
    ): String? {

        val body = response.body() ?: return null

        val disposition = response.headers()["Content-Disposition"]
        val filename = disposition
            ?.substringAfter("filename=")
            ?.replace("\"", "")
            ?: "result_${System.currentTimeMillis()}.bin"

        val file = File(context.getExternalFilesDir(null), filename)

        try {
            body.byteStream().use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            Log.d("DEBUG_FILE", "File saved as: ${file.absolutePath}")
            return file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}