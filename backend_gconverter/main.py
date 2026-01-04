from fastapi import FastAPI
from database import Base, engine

from routers import auth, payment, convert

Base.metadata.create_all(bind=engine)

app = FastAPI(title="Converter Backend")

app.include_router(auth.router)
app.include_router(payment.router)
app.include_router(convert.router)
