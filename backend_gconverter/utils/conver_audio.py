import os, subprocess, uuid
from utils.job_store import update_job, finish_job, jobs

def convert_mp3_to_wav(
    input_audio_path: str,
    output_dir: str,
    job_id: str
) -> str:
    os.makedirs(output_dir, exist_ok=True)

    base_name = os.path.splitext(os.path.basename(input_audio_path))[0]
    output_path = os.path.join(
        output_dir, f"{base_name}_{uuid.uuid4().hex}.wav"
    )

    cmd = (
        "ffmpeg",
        "-y",
        "-i", input_audio_path,
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
            progress = min(90, jobs[job_id]["progress"] + 1)
            update_job(job_id, progress)

    process.wait()
    finish_job(job_id, output_path)

    return output_path


def convert_wav_to_mp3(
    input_audio_path: str,
    output_dir: str,
    job_id: str
) -> str:
    os.makedirs(output_dir, exist_ok=True)

    base_name = os.path.splitext(os.path.basename(input_audio_path))[0]
    output_path = os.path.join(
        output_dir, f"{base_name}_{uuid.uuid4().hex}.mp3"
    )

    cmd = (
        "ffmpeg",
        "-y",
        "-i", input_audio_path,
        "-c:a", "libmp3lame",
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
            progress = min(90, jobs[job_id]["progress"] + 1)
            update_job(job_id, progress)

    process.wait()
    finish_job(job_id, output_path)

    return output_path
