# app/services/quantum_service.py
from typing import Dict
from app.quantum.bb84 import run_bb84

def generate_bb84_keys(n: int, with_eve: bool = False) -> Dict:
    """
    Wrapper around the BB84 protocol simulator in bb84.py
    Returns the full protocol results as a dictionary.
    """
    return run_bb84(length=n, with_eve=with_eve)
