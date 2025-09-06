# app/quantum/qber.py
from typing import List, Tuple
import math

def calculate_qber(alice_key: List[int], bob_key: List[int]) -> float:
    """
    Calculate Quantum Bit Error Rate between two keys (lists of ints 0/1).
    Returns a float between 0 and 1. If no bits, returns 0.0.
    """
    if not alice_key or not bob_key:
        return 0.0
    length = min(len(alice_key), len(bob_key))
    if length == 0:
        return 0.0
    errors = sum(1 for a, b in zip(alice_key[:length], bob_key[:length]) if a != b)
    return errors / length
