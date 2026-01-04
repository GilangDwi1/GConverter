from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
import uuid
from datetime import datetime, timedelta

from models import Transaction
from dependencies import get_current_user, get_db

router = APIRouter(prefix="/payment", tags=["Payment"])


@router.post("/create")
def create_payment(user=Depends(get_current_user), db: Session = Depends(get_db)):
    trx = Transaction(
        user_id=user.id,
        status="PENDING",
        amount="10000"
    )
    db.add(trx)
    db.commit()
    db.refresh(trx)

    return {
        "payment_id": trx.id,
        "payment_url": f"https://finpay.test/pay/{trx.id}"
    }


@router.post("/callback")
def payment_callback(payment_id: str, status: str, db: Session = Depends(get_db)):
    trx = db.query(Transaction).filter(Transaction.id == payment_id).first()

    if not trx:
        return {"error": "not found"}

    if status == "PAID":
        trx.status = "PAID"
        trx.user.is_premium = True
        trx.user.premium_expired_at = datetime.utcnow() + timedelta(days=30)

    db.commit()
    return {"message": "ok"}
