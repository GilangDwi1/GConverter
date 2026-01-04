// D:/Save-an/G-Converter/app/src/main/java/utils/LimitManager.kt
package utils

import android.content.Context

object LimitManager {
    private const val PREF_NAME = "user_limit_prefs"
    private const val KEY_UPLOAD_COUNT = "upload_count"
    private const val MAX_FREE_LIMIT = 3

    // Cek apakah user boleh upload
    fun canUpload(context: Context): Boolean {
        val session = SessionManager(context)

        // Jika Premium, abaikan limit
        if (session.isPremium()) return true

        // Jika tidak premium, cek jumlah upload di local
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val currentCount = prefs.getInt(KEY_UPLOAD_COUNT, 0)
        return currentCount < MAX_FREE_LIMIT
    }

    // Tambah hitungan setelah upload berhasil
    fun incrementCount(context: Context) {
        val session = SessionManager(context)

        // Hanya tambah hitungan jika user BUKAN premium
        if (!session.isPremium()) {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val currentCount = prefs.getInt(KEY_UPLOAD_COUNT, 0)
            prefs.edit().putInt(KEY_UPLOAD_COUNT, currentCount + 1).apply()
        }
    }

    fun getRemainingLimit(context: Context): Int {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val currentCount = prefs.getInt(KEY_UPLOAD_COUNT, 0)
        return (MAX_FREE_LIMIT - currentCount).coerceAtLeast(0)
    }
}
