# utils/job_store.py
from threading import Lock

jobs = {}
lock = Lock()

def create_job(job_id):
    with lock:
        jobs[job_id] = {
            "status": "processing",
            "progress": 0,
            "output": None
        }

def update_job(job_id, progress):
    with lock:
        if job_id in jobs:
            jobs[job_id]["progress"] = progress

def finish_job(job_id, output):
    with lock:
        if job_id in jobs:
            jobs[job_id]["status"] = "done"
            jobs[job_id]["progress"] = 100
            jobs[job_id]["output"] = output

def get_job(job_id):
    with lock:
        return jobs.get(job_id)
