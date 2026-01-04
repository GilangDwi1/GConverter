package api.settings

import AuthRequest
import AuthResponse
import JobResponse
import JobStatusResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Convert MP4 to MOV
    @Multipart
    @POST("/convert/convert/mp4-to-mov")
    suspend fun mp4ToMov(
        @Part file: MultipartBody.Part
    ): Response<JobResponse>

    // Convert MOV to MP4
    @Multipart
    @POST("/convert/convert/mov-to-mp4")
    suspend fun  movToMp4(
        @Part file: MultipartBody.Part
    ): Response<JobResponse>

    // Convert JPG To PNG

    @Multipart
    @POST("/convert/convert/jpg-to-png")
    suspend fun jpgToPng(
        @Part file: MultipartBody.Part
    ): Response<JobResponse>

    // Convert PNG To JPG
    @Multipart
    @POST("/convert/convert/png-to-jpeg")
    suspend fun pngToJpg(
        @Part file: MultipartBody.Part
    ): Response<JobResponse>

    // Convert JPG To Webp
    @Multipart
    @POST("/convert/convert/jpg-to-webp")
    suspend fun jpgToWebp(
        @Part file: MultipartBody.Part
    ): Response<JobResponse>

    // Convert PNG To Webp
    @Multipart
    @POST("/convert/convert/png-to-webp")
    suspend fun pngToWebp(
        @Part file: MultipartBody.Part
    ): Response<JobResponse>

    //convert Webp To PNG
    @Multipart
    @POST("/convert/convert/webp-to-png")
    suspend fun webpToPng(
        @Part file: MultipartBody.Part
    ): Response<JobResponse>

    //convert WEBP to JPG
    @Multipart
    @POST("/convert/convert/webp-to-jpeg")
    suspend fun webpToJpg(
        @Part file: MultipartBody.Part
    ): Response<JobResponse>

    //convert PDF to WORD
    @Multipart
    @POST("/convert/convert/pdf-to-docx")
    suspend fun pdfToWord(
        @Part file: MultipartBody.Part
    ): Response<JobResponse>

    // Convert WORD to PDF
    @Multipart
    @POST("/convert/convert/docx-to-pdf")
    suspend fun wordToPdf(
        @Part file: MultipartBody.Part
    ): Response<JobResponse>

    // Convert XLSX to PDF
    @Multipart
    @POST("/convert/convert/xlsx-to-pdf")
    suspend fun xlsxToPdf(
        @Part file: MultipartBody.Part
    ): Response<JobResponse>

    // Convert WAV to MP3
    @Multipart
    @POST("/convert/convert/wav-to-mp3")
    suspend fun wavToMp3(
        @Part file: MultipartBody.Part
    ): Response<JobResponse>


    // Convert MP3 to WAV
    @Multipart
    @POST("/convert/convert/mp3-to-wav")
    suspend fun mp3ToWav(
        @Part file: MultipartBody.Part
    ): Response<JobResponse>

    @GET("/convert/convert/status/{jobId}")
    suspend fun checkStatus(
        @Path("jobId") jobId: String
    ): Response<JobStatusResponse>

    @GET("/convert/convert/download/{jobId}")
    suspend fun downloadFile(
        @Path("jobId") jobId: String
    ): Response<ResponseBody>

    // AUTH
    @POST("/auth/register")
    suspend fun register(
        @Body request: AuthRequest
    ): Response<Void>

    @POST("/auth/login")
    suspend fun login(
        @Body request: AuthRequest
    ): Response<AuthResponse>
}
