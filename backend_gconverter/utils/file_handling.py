import os, uuid, shutil
from fastapi import UploadFile

SPREADSHEET_MIME_TYPES = {
    ".xlsx": {
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "application/octet-stream",
        "application/zip",
    },
    ".xls": {
        "application/vnd.ms-excel",
        "application/octet-stream",
    },
    ".csv": {
        "text/csv",
        "application/csv",
        "application/octet-stream",
    },
    ".ods": {
        "application/vnd.oasis.opendocument.spreadsheet",
        "application/octet-stream",
        "application/zip",
    },
}

def save_uploaded_file(
    file: UploadFile,
    base_dir: str,
) -> str:
    os.makedirs(base_dir, exist_ok=True)

    name, ext = os.path.splitext(file.filename)
    ext = ext.lower()                                                           

    filename = f"{uuid.uuid4()}{ext}"
    file_path = os.path.join(base_dir, filename)

    with open(file_path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)

    return file_path

def validate_spreadsheet(file: UploadFile) -> tuple[str, str]:
    """
    Validasi file spreadsheet berdasarkan extension & content type.

    Returns:
        (ext, mime_type)

    Raises:
        ValueError jika tidak valid
    """
    if not file.filename:
        raise ValueError("Filename is missing")

    _, ext = os.path.splitext(file.filename)
    ext = ext.lower()

    if ext not in SPREADSHEET_MIME_TYPES:
        raise ValueError(f"Unsupported spreadsheet extension: {ext}")

    content_type = (file.content_type or "").lower()

    allowed_mimes = SPREADSHEET_MIME_TYPES[ext]

    # MIME boleh kosong atau generic
    if content_type and content_type not in allowed_mimes:
        raise ValueError(
            f"Invalid MIME type for {ext}: {content_type}"
        )

    return ext, content_type