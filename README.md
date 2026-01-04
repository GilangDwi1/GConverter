# 📁 G-Converter

**G-Converter** adalah aplikasi Android yang berfungsi untuk **mengonversi berbagai jenis file** seperti **image, video, audio, dan dokumen**.  
Aplikasi ini dibangun menggunakan **Kotlin** pada sisi Android dan **FastAPI (Python)** sebagai backend untuk menangani proses konversi file secara asynchronous dan efisien.

Project ini dirancang sebagai **aplikasi client–server**, di mana Android berperan sebagai client dan FastAPI sebagai server pemrosesan file.

---

## 🎯 Tujuan Project

- Menyediakan solusi konversi file lintas format dalam satu aplikasi
- Mengimplementasikan arsitektur backend modern menggunakan FastAPI
- Menerapkan job-based processing untuk file conversion
- Mendukung pengembangan aplikasi Android berbasis REST API

---

## ✨ Fitur Utama

### 📷 Image Conversion
- JPG → PNG
- PNG → JPG

### 🎥 Video Conversion
- MP4 → MOV
- MOV → MP4

### 🎧 Audio Conversion
- MP3 → WAV
- WAV → MP3

### 📄 Document Conversion
- PDF → DOCX
- DOCX → PDF
- XLSX → PDF

### 🔐 Authentication
- Register
- Login
- JWT Token Authentication

### ⚙️ Sistem
- Upload file melalui Android
- Job ID untuk setiap proses konversi
- Monitoring status konversi
- Download hasil konversi
- Penyimpanan file sementara di server

---

## 🏗️ Arsitektur Sistem

Android AppKotlin
HTTP (REST API)

▼

FastAPI Backend │ Python
-Authentication │
- File Upload │
- Conversion Job │
▼
Conversion Engine
FFmpeg / LibreOffice


---

## 🛠️ Teknologi yang Digunakan

### Android (Frontend)
- Kotlin
- Retrofit
- OkHttp
- Coroutine
- Material Design
- Android SDK

### Backend
- Python 3.10+
- FastAPI
- Uvicorn
- FFmpeg
- LibreOffice (Headless)
- Passlib
- JWT (python-jose)

---

## 📂 Struktur Folder

g-converter/
│
├── android/
│ └── app/
│
├── backend/
│ ├── routers/
│ │ ├── auth.py
│ │ └── convert.py
│ ├── services/
│ ├── utils/
│ ├── main.py
│ └── requirements.txt
│
└── README.md

yaml
Copy code

---

## 🚀 Instalasi Backend (FastAPI)

### 1️⃣ Clone Repository

```bash
git clone https://github.com/username/g-converter.git
cd g-converter/backend
2️⃣ Buat Virtual Environment
bash
Copy code
python -m venv .venv
Aktifkan virtual environment:

Windows

bash
Copy code
.venv\Scripts\activate
Linux / macOS

bash
Copy code
source .venv/bin/activate
3️⃣ Install Dependencies
bash
Copy code
pip install -r requirements.txt
Disarankan menggunakan versi berikut untuk stabilitas:

txt
Copy code
fastapi
uvicorn
python-jose
passlib==1.7.4
bcrypt==3.2.2
4️⃣ Install Dependency Sistem
FFmpeg
Digunakan untuk konversi audio dan video.

Download: https://ffmpeg.org/download.html

Tambahkan FFmpeg ke PATH

Cek instalasi:

bash
Copy code
ffmpeg -version
LibreOffice (Headless)
Digunakan untuk konversi dokumen.

Download: https://www.libreoffice.org/download/

Pastikan perintah berikut bisa dijalankan:

bash
Copy code
soffice --version
5️⃣ Jalankan Backend
bash
Copy code
uvicorn main:app --reload
Akses backend:

API: http://127.0.0.1:8000

Swagger UI: http://127.0.0.1:8000/docs

📱 Instalasi Aplikasi Android
1️⃣ Buka Project Android
Buka Android Studio

Pilih Open

Arahkan ke folder android/

2️⃣ Konfigurasi Base URL
Ubah Base URL Retrofit:

kotlin
Copy code
const val BASE_URL = "http://10.0.2.2:8000/"
Keterangan:

10.0.2.2 → Android Emulator

IP Lokal → Device fisik

Ngrok URL → Akses publik

3️⃣ Jalankan Aplikasi
Pilih emulator atau device

Klik Run ▶

Aplikasi siap digunakan

🔐 Alur Authentication
User melakukan Register

Password di-hash menggunakan bcrypt

User Login

Server mengembalikan JWT Token

Token digunakan untuk request konversi

🌐 Contoh Endpoint API
/auth/register
/auth/login
/convert/convert/pdf-to-docx/
/convert/convert/docx-to-pdf/
/convert/convert/xlsx-to-pdf/
/convert/convert/jpg-to-webp
/convert/convert/png-to-jpeg
/convert/convert/png-to-webp
/convert/convert/webp-to-png
/convert/convert/webp-to-jpeg
/convert/convert/mp4-to-mov
/convert/convert/mov-to-mp4
/convert/convert/wav-to-mp3
/convert/convert/wav-to-mp3
/convert/convert/status/{job_id}
/convert/convert/download/{job_id}
🧪 Contoh Alur Konversi
Upload file

Server membuat Job ID

File diproses di background

Client cek status

File hasil konversi diunduh

⚠️ Troubleshooting
❌ FFmpeg not found
Pastikan FFmpeg sudah ditambahkan ke PATH.

❌ LibreOffice error
Pastikan LibreOffice terinstall dan dapat dijalankan via terminal.

❌ Backend tidak bisa diakses dari Android
Gunakan IP lokal

Nonaktifkan firewall sementara

Gunakan Ngrok jika perlu

👨‍💻 Kontributor
Nama: Gilang Dwi

Role: Android Developer & Backend Developer

📜 Lisensi
Project ini dibuat untuk keperluan edukasi dan pengembangan.
Bebas digunakan dan dimodifikasi sesuai kebutuhan.

🔥 G-Converter — One App for All File Conversions
## Documentation

[Documentation](https://linktodocumentation)


## API Reference

#### Get all items

```http
  GET /api/items
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `api_key` | `string` | **Required**. Your API key |

#### Get item

```http
  GET /api/items/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `string` | **Required**. Id of item to fetch |

#### add(num1, num2)

Takes two numbers and returns the sum.

