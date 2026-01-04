from fastapi import APIRouter, Depends, HTTPException, File, UploadFile, Form
from utils.convert_image import convert_jpg_to_png, convert_jpg_to_webp, convert_png_to_jpg, convert_png_to_webp, convert_webp_to_png, convert_webp_to_jpeg
from utils.convert_video import convert_mp4_to_mov, convert_mov_to_mp4
from utils.convert_file import convert_pdf_to_docx, convert_docx_to_pdf, convert_xlsx_to_pdf
from utils.conver_audio import convert_mp3_to_wav, convert_wav_to_mp3
from utils.file_handling import save_uploaded_file, validate_spreadsheet
from fastapi.concurrency import run_in_threadpool
from fastapi.responses import FileResponse
from fastapi.background import BackgroundTasks
from utils.job_store import get_job, create_job
from dependencies import get_current_user
import uuid, os, mimetypes

router = APIRouter(prefix="/convert", tags=["Convert"])
jobs = {}

UPLOADS_DIR = "uploads"
OUTPUTS_DIR = "outputs"

@router.get("/")
def read_root():
    return {"message": "Welcome to the Converter API"}

@router.post("/video")
def convert_video(user=Depends(get_current_user)):
    if not user.is_premium:
        raise HTTPException(403, "Premium required")

    return {"message": "video converted (dummy)"}

# Endpoint to handle file uploads and convert PDF to DOCX
@router.post("/convert/pdf-to-docx/")
async def start_convert_pdf_to_docx(
    background_tasks: BackgroundTasks,
    file: UploadFile = File(...)
):
    if file.content_type != "application/pdf":
        raise HTTPException(status_code=400, detail="Invalid file type")

    job_id = str(uuid.uuid4())
    create_job(job_id)

    input_path = save_uploaded_file(
        file,
        UPLOADS_DIR
    )

    background_tasks.add_task(
        convert_pdf_to_docx,
        input_path,
        OUTPUTS_DIR,
        job_id
    )

    return {"job_id": job_id}

@router.post("/convert/docx-to-pdf/")
async def start_convert_docx_to_pdf(
    background_tasks: BackgroundTasks,
    file: UploadFile = File(...)
):
    if file.content_type != (
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    ):
        raise HTTPException(status_code=400, detail="Invalid content type")

    job_id = str(uuid.uuid4())
    create_job(job_id)

    input_path = save_uploaded_file(
        file,
        UPLOADS_DIR,
    )

    background_tasks.add_task(
        convert_docx_to_pdf,
        input_path,
        OUTPUTS_DIR,
        job_id
    )

    return {"job_id": job_id}


# Conver xlsx to pdf
@router.post("/convert/xlsx-to-pdf/")
async def start_convert_xlsx_to_pdf(
    background_tasks: BackgroundTasks,
    file: UploadFile = File(...)
):
    try:
        ext, mime = validate_spreadsheet(file)
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))

    job_id = str(uuid.uuid4())
    create_job(job_id)

    input_path = save_uploaded_file(
        file,
        UPLOADS_DIR,
    )

    background_tasks.add_task(
        convert_xlsx_to_pdf,
        input_path,
        OUTPUTS_DIR,
        job_id
    )

    return {"job_id": job_id}

# Conver jpg to png
@router.post("/convert/jpg-to-png/")
async def start_convert(
    background_tasks: BackgroundTasks,
    file: UploadFile = File(...)
):
    job_id = str(uuid.uuid4())
    create_job(job_id)

    input_path = save_uploaded_file(file, UPLOADS_DIR)
    output_path = os.path.join(OUTPUTS_DIR, f"{job_id}.png")

    background_tasks.add_task(
        convert_jpg_to_png,
        input_path,
        output_path,
        job_id
    )

    return {"job_id": job_id}

# Convert jpg to webp
@router.post("/convert/jpg-to-webp")
async def start_convert_jpg_to_webp(
    background_tasks: BackgroundTasks,
    file: UploadFile = File(...),
    mode: str = Form("lossy"),
    quality: int = Form(80)
):
    if file.content_type != "image/jpeg":
        raise HTTPException(status_code=400, detail="Invalid Content Type")

    job_id = str(uuid.uuid4())
    create_job(job_id)

    input_path = save_uploaded_file(file, UPLOADS_DIR)

    background_tasks.add_task(
        convert_jpg_to_webp,
        input_path,
        OUTPUTS_DIR,
        job_id,
        mode,
        quality
    )

    return {"job_id": job_id}

# Convert PNG to JPEG
@router.post("/convert/png-to-jpeg")
async def start_convert_png_to_jpeg(
    background_tasks: BackgroundTasks,
    file: UploadFile = File(...)
):
    if file.content_type != "image/png":
        raise HTTPException(status_code=400, detail="Invalid Content Type")

    job_id = str(uuid.uuid4())
    create_job(job_id)

    input_path = save_uploaded_file(file, UPLOADS_DIR)

    background_tasks.add_task(
        convert_png_to_jpg,
        input_path,
        OUTPUTS_DIR,
        job_id
    )

    return {"job_id": job_id}

@router.post("/convert/png-to-webp")
async def start_convert_png_to_webp(
    background_tasks: BackgroundTasks,
    file: UploadFile = File(...),
    mode: str = Form("lossy"),
    quality: int = Form(80)
):
    if file.content_type != "image/png":
        raise HTTPException(status_code=400, detail="Invalid Content Type")

    job_id = str(uuid.uuid4())
    create_job(job_id)

    input_path = save_uploaded_file(file, UPLOADS_DIR)

    background_tasks.add_task(
        convert_png_to_webp,
        input_path,
        OUTPUTS_DIR,
        job_id,
        mode,
        quality
    )

    return {"job_id": job_id}

@router.post("/convert/webp-to-png")
async def start_convert_webp_to_png(
    background_tasks: BackgroundTasks,
    file: UploadFile = File(...),
    quality: int = Form(80)
):
    if file.content_type != "image/webp":
        raise HTTPException(status_code=400, detail="Invalid Content Type")

    job_id = str(uuid.uuid4())
    create_job(job_id)

    input_path = save_uploaded_file(file, UPLOADS_DIR)
    print(input_path)
    background_tasks.add_task(
        convert_webp_to_png,
        input_path,
        OUTPUTS_DIR,
        job_id,
        quality
    )

    return {"job_id": job_id}

@router.post("/convert/webp-to-jpeg")
async def start_convert_webp_to_jpeg(
    background_tasks: BackgroundTasks,
    file: UploadFile = File(...)
):
    if file.content_type != "image/webp":
        raise HTTPException(status_code=400, detail="Invalid Content Type")

    job_id = str(uuid.uuid4())
    create_job(job_id)

    input_path = save_uploaded_file(file, UPLOADS_DIR)

    background_tasks.add_task(
        convert_webp_to_jpeg,
        input_path,
        OUTPUTS_DIR,
        job_id
    )

    return {"job_id": job_id}

@router.post("/convert/mp4-to-mov")
async def start_convert(
    background_tasks: BackgroundTasks,
    file: UploadFile = File(...)
):
    job_id = str(uuid.uuid4())
    create_job(job_id)

    input_path = save_uploaded_file(file, UPLOADS_DIR)
    output_path = os.path.join(OUTPUTS_DIR, f"{job_id}.mov")

    background_tasks.add_task(
        convert_mp4_to_mov,
        input_path,
        output_path,
        job_id
    )

    return {"job_id": job_id}


@router.post("/convert/mov-to-mp4")
async def start_convert(
    background_tasks: BackgroundTasks,
    file: UploadFile = File(...)
):
    job_id = str(uuid.uuid4())
    create_job(job_id)

    input_path = save_uploaded_file(file, UPLOADS_DIR)
    output_path = os.path.join(OUTPUTS_DIR, f"{job_id}.mp4")

    background_tasks.add_task(
        convert_mov_to_mp4,
        input_path,
        output_path,
        job_id
    )

    return {"job_id": job_id}

@router.post("/convert/wav-to-mp3")
async def start_convert_wav_to_mp3(
    background_tasks: BackgroundTasks,
    file: UploadFile = File(...)
):
    if file.content_type not in ("audio/wav", "audio/x-wav"):
        raise HTTPException(status_code=400, detail="Invalid Content Type")

    job_id = str(uuid.uuid4())
    create_job(job_id)

    input_path = save_uploaded_file(file, UPLOADS_DIR)

    background_tasks.add_task(
        convert_wav_to_mp3,
        input_path,
        OUTPUTS_DIR,
        job_id
    )

    return {"job_id": job_id}

@router.post("/convert/mp3-to-wav")
async def start_convert_mp3_to_wav(
    background_tasks: BackgroundTasks,
    file: UploadFile = File(...)
):
    if file.content_type != "audio/mpeg":
        raise HTTPException(status_code=400, detail="Invalid Content Type")

    job_id = str(uuid.uuid4())
    create_job(job_id)

    input_path = save_uploaded_file(file, UPLOADS_DIR)

    background_tasks.add_task(
        convert_mp3_to_wav,
        input_path,
        OUTPUTS_DIR,
        job_id
    )

    return {"job_id": job_id}


@router.get("/convert/status/{job_id}")
def get_status(job_id: str):
    job = get_job(job_id)

    if not job:
        raise HTTPException(status_code=404, detail="Job not found")

    return {
        "status": job["status"],
        "progress": job["progress"]
    }

@router.get("/convert/download/{job_id}")
def download(job_id: str):
    job = get_job(job_id)

    if not job:
        raise HTTPException(status_code=404, detail="Job not found")

    if job["status"] != "done":
        raise HTTPException(status_code=400, detail="Job not finished")

    if not job["output"]:
        raise HTTPException(status_code=500, detail="Output file missing")

    output_path = job["output"]
    # Otomatis deteksi media_type (misal: video/mp4, video/quicktime)
    mime_type, _ = mimetypes.guess_type(output_path)
    print (output_path, mime_type)
    return FileResponse(
        output_path,
        filename=os.path.basename(output_path),
        media_type=mime_type or "application/octet-stream"
    )

