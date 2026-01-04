package utils

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveAuthToken(token: String) {
        prefs.edit().putString("auth_token", token).apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString("auth_token", null)
    }

    fun setPremiumStatus(isPremium: Boolean) {
        prefs.edit().putBoolean("is_premium", isPremium).apply()
    }

    fun isPremium(): Boolean {
        return prefs.getBoolean("is_premium", false)
    }

    fun isLoggedIn(): Boolean {
        return fetchAuthToken() != null
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}
