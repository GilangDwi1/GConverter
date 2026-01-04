import subprocess ,os ,uuid
import subprocess
from utils.job_store import update_job, finish_job, jobs

def convert_mp4_to_mov(input_video_path : str, output_dir : str, job_id : str) -> str:
    os.makedirs(output_dir, exist_ok=True)

    base_name = os.path.splitext(os.path.basename(input_video_path))[0]
    output_path = os.path.join(output_dir, f"{base_name}_{uuid.uuid4().hex}.mov")

    cmd = (
        "ffmpeg",
        "-y",
        "-i", input_video_path,
        "-c:v", "libx264",
        "-pix_fmt", "yuv420p",
        "-c:a", "aac",
        output_path
    )

    process = subprocess.Popen(
        cmd,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True
    )

    for line in process.stdout:
        if "out_time_ms" in line:
            # contoh parsing kasar
            progress = min(90, jobs[job_id]["progress"] + 1)
            update_job(job_id, progress)

    process.wait()
    finish_job(job_id, output_path)

def convert_mov_to_mp4(input_video_path : str, output_dir : str, job_id : str) -> str:
    os.makedirs(output_dir, exist_ok=True)

    base_name = os.path.splitext(os.path.basename(input_video_path))[0]
    output_path = os.path.join(output_dir, f"{base_name}_{uuid.uuid4().hex}.mp4")

    cmd = (
        "ffmpeg",
        "-y",
        "-i", input_video_path,
        "-c:v", "libx264",
        "-pix_fmt", "yuv420p",
        "-c:a", "aac",
        output_path
    )

    process = subprocess.Popen(
        cmd,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True
    )

    for line in process.stdout:
        if "out_time_ms" in line:
            # contoh parsing kasar
            progress = min(90, jobs[job_id]["progress"] + 1)
            update_job(job_id, progress)

    process.wait()
    print(output_path)
    finish_job(job_id, output_path)

