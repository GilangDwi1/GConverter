from PIL import Image, ImageOps
import os, uuid
from utils.job_store import update_job, finish_job

# Convert JPG to PNG
def convert_jpg_to_png(input_image_path: str, output_dir: str, job_id: str) -> str:
    os.makedirs(output_dir, exist_ok=True)

    base_name = os.path.splitext(os.path.basename(input_image_path))[0]
    output_path = os.path.join(
        output_dir, f"{base_name}_{uuid.uuid4().hex}.png"
    )

    update_job(job_id, 10)

    with Image.open(input_image_path) as img:
        img = ImageOps.exif_transpose(img)
        update_job(job_id, 40)

        img = img.convert("RGB")
        update_job(job_id, 70)

        img.save(output_path, format="PNG")
        update_job(job_id, 90)

    finish_job(job_id, output_path)
    return output_path

def convert_jpg_to_webp(
    input_image_path: str,
    output_dir: str,
    job_id: str,
    mode: str,
    quality: int
) -> str:
    os.makedirs(output_dir, exist_ok=True)

    base_name = os.path.splitext(os.path.basename(input_image_path))[0]
    output_path = os.path.join(
        output_dir, f"{base_name}_{uuid.uuid4().hex}.webp"
    )

    update_job(job_id, 10)

    with Image.open(input_image_path) as img:
        img = img.convert("RGB")
        update_job(job_id, 50)

        if mode == "lossy":
            img.save(output_path, format="WEBP", lossless=True)
        else:
            quality = max(1, min(int(quality), 100))
            img.save(output_path, format="WEBP", quality=quality)

        update_job(job_id, 90)

    finish_job(job_id, output_path)
    return output_path

def convert_png_to_jpg(
    input_image_path: str,
    output_dir: str,
    job_id: str
) -> str:
    os.makedirs(output_dir, exist_ok=True)

    base_name = os.path.splitext(os.path.basename(input_image_path))[0]
    output_path = os.path.join(
        output_dir, f"{base_name}_{uuid.uuid4().hex}.jpg"
    )

    update_job(job_id, 10)

    with Image.open(input_image_path) as img:
        if img.mode in ("RGBA", "LA"):
            background = Image.new("RGB", img.size, (255, 255, 255))
            background.paste(img, mask=img.split()[-1])
            img = background
        else:
            img = img.convert("RGB")

        update_job(job_id, 70)
        img.save(output_path, format="JPEG", quality=95)
        update_job(job_id, 90)

    finish_job(job_id, output_path)
    return output_path

def convert_png_to_webp(
    input_image_path: str,
    output_dir: str,
    job_id: str,
    mode: str,
    quality: int
) -> str:
    os.makedirs(output_dir, exist_ok=True)

    base_name = os.path.splitext(os.path.basename(input_image_path))[0]
    output_path = os.path.join(
        output_dir, f"{base_name}_{uuid.uuid4().hex}.webp"
    )

    update_job(job_id, 10)

    with Image.open(input_image_path) as img:
        update_job(job_id, 50)

        if mode == "lossy":
            quality = max(1, min(int(quality), 100))
            img.save(output_path, format="WEBP", quality=quality)
        else:
            img.save(output_path, format="WEBP", lossless=True)

        update_job(job_id, 90)

    finish_job(job_id, output_path)
    return output_path

def convert_webp_to_png(
    input_image_path: str,
    output_dir: str,
    job_id: str,
    quality: int
) -> str:
    os.makedirs(output_dir, exist_ok=True)

    base_name = os.path.splitext(os.path.basename(input_image_path))[0]
    output_path = os.path.join(
        output_dir, f"{base_name}_{uuid.uuid4().hex}.png"
    )

    update_job(job_id, 10)

    with Image.open(input_image_path) as img:
        update_job(job_id, 60)

        if img.mode not in ("RGBA", "LA"):
            img = img.convert("RGB")

        img.save(output_path, format="PNG", quality=quality)
        update_job(job_id, 90)

    finish_job(job_id, output_path)
    return output_path

def convert_webp_to_jpeg(
    input_image_path: str,
    output_dir: str,
    job_id: str
) -> str:
    os.makedirs(output_dir, exist_ok=True)

    base_name = os.path.splitext(os.path.basename(input_image_path))[0]
    output_path = os.path.join(
        output_dir, f"{base_name}_{uuid.uuid4().hex}.jpeg"
    )

    update_job(job_id, 10)

    with Image.open(input_image_path) as img:
        if img.mode in ("RGBA", "LA"):
            background = Image.new("RGB", img.size, (255, 255, 255))
            background.paste(img, mask=img.split()[-1])
            img = background
        else:
            img = img.convert("RGB")

        update_job(job_id, 70)
        img.save(output_path, format="JPEG", quality=95)
        update_job(job_id, 90)

    finish_job(job_id, output_path)
    return output_path