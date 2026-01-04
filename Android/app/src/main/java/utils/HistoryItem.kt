package utils
import java.io.File

data class HistoryItem(
    val name: String,
    val path: String,
    val size: String,
    val date: String,
    val file: File
)
