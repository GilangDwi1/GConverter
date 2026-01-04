data class JobResponse(
    val job_id: String
)

data class JobStatusResponse(
    val status: String,
    val progress: Int
)

data class HistoryItem(
    val jobId: String,
    val type: String,
    val status: String
)
