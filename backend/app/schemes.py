# app/schemas.py
from pydantic import BaseModel
from typing import List, Optional

class BB84Response(BaseModel):
    length: int
    alice_bits: List[int]
    alice_bases: List[str]
    bob_bases: List[str]
    bob_results: List[int]
    matching_indices: List[int]
    alice_key: List[int]
    bob_key: List[int]
    qber: float
    eve_bases: Optional[List[str]] = None
    eve_results: Optional[List[int]] = None
