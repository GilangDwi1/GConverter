from utils.job_store import update_job, finish_job, jobs
from pdf2docx import Converter
from docx2pdf import convert
import os, subprocess, uuid




UPLOADS_DIR = "uploads"
OUTPUTS_DIR = "outputs"

# convert PDF to DOCX
def convert_pdf_to_docx(
    pdf_path: str,
    output_dir: str,
    job_id: str
) -> str:
    os.makedirs(output_dir, exist_ok=True)

    base_name = os.path.splitext(os.path.basename(pdf_path))[0]
    output_path = os.path.join(
        output_dir, f"{base_name}_{uuid.uuid4().hex}.docx"
    )

    update_job(job_id, 10)

    cv = Converter(pdf_path)
    try:
        cv.convert(output_path)
        update_job(job_id, 90)
    finally:
        cv.close()

    finish_job(job_id, output_path)
    return output_path

# convert DOCX to PDF
def convert_docx_to_pdf(
    docx_path: str,
    output_dir: str,
    job_id: str
) -> str:
    os.makedirs(output_dir, exist_ok=True)

    base_name = os.path.splitext(os.path.basename(docx_path))[0]
    output_path = os.path.join(
        output_dir, f"{base_name}_{uuid.uuid4().hex}.pdf"
    )

    update_job(job_id, 10)

    convert(docx_path, output_path)
    update_job(job_id, 90)

    finish_job(job_id, output_path)
    return output_path

#convert XLSX to PDF
def convert_xlsx_to_pdf(
    xlsx_path: str,
    output_dir: str,
    job_id: str
) -> str:
    os.makedirs(output_dir, exist_ok=True)

    base_name = os.path.splitext(os.path.basename(xlsx_path))[0]
    output_path = os.path.join(
        output_dir, f"{base_name}_{uuid.uuid4().hex}.pdf"
    )

    update_job(job_id, 10)

    process = subprocess.Popen(
        [
            "soffice",
            "--headless",
            "--convert-to", "pdf",
            xlsx_path,
            "--outdir", output_dir
        ],
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True
    )

    for _ in process.stdout:
        progress = min(90, jobs[job_id]["progress"] + 5)
        update_job(job_id, progress)

    process.wait()

    # LibreOffice pakai nama asli → rename ke output_path
    original_pdf = os.path.join(output_dir, base_name + ".pdf")
    if os.path.exists(original_pdf):
        os.rename(original_pdf, output_path)

    finish_job(job_id, output_path)
    return output_path

