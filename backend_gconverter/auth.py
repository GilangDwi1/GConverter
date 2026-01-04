from pydantic import BaseModel, Field

class RegisterRequest(BaseModel):
    email: str
    password: str = Field(min_length=8, max_length=128)
