from fastapi import APIRouter
from app.services.quantum_service import generate_bb84_keys

router = APIRouter()

@router.get("/keys")
async def get_quantum_keys(n: int = 10, with_eve: bool = False):
   
    return generate_bb84_keys(n, with_eve)
