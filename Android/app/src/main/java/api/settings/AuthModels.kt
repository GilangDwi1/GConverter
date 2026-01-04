// AuthModels.kt
data class AuthRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val access_token: String, // Menyesuaikan standar FastAPI OAuth2
    val token_type: String,
    val is_premium: Boolean // Tambahkan ini di backend jika ingin membedakan status
)

