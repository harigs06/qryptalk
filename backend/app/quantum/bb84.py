# app/quantum/bb84.py
from typing import List, Tuple, Optional, Dict
import secrets
from qiskit import QuantumCircuit, ClassicalRegister, QuantumRegister, transpile
# from qiskit.providers.aer import AerSimulator


# Use AerSimulator for local simulation
# SIM = AerSimulator()

def random_bits_and_bases(length: int) -> Tuple[List[int], List[str]]:
    """Generate random bit string and bases list ('+' or 'x')."""
    bits = [secrets.randbelow(2) for _ in range(length)]
    bases = [secrets.choice(['+', 'x']) for _ in range(length)]
    return bits, bases

def _prepare_and_measure_single_qubit(alice_bit: int, alice_basis: str, bob_basis: str,
                                     eve_basis: Optional[str] = None) -> Tuple[int, Optional[int]]:
    """
    Simulate the full lifecycle of a single qubit:
    - Alice prepares according to (alice_bit, alice_basis)
    - Optionally, Eve measures in eve_basis and re-prepares
    - Bob measures in bob_basis and returns bob_result and eve_result (if applicable)
    """
    # We'll use sequential single-qubit circuits so we can simulate Eve's intermediate measurement
    # Step 1: Alice prepares
    q = QuantumRegister(1, "q")
    c = ClassicalRegister(1, "c")
    qc = QuantumCircuit(q, c)

    # Prepare Alice's bit in computational basis, then apply H if basis is 'x'
    if alice_bit == 1:
        qc.x(q[0])
    if alice_basis == 'x':
        qc.h(q[0])

    # If Eve exists: measure then re-prepare according to her measurement outcome & her basis
    eve_outcome = None
    if eve_basis is not None:
        # Measure in Eve's chosen basis:
        if eve_basis == 'x':
            qc.h(q[0])
        qc.measure(q[0], c[0])
        # run
        transpiled = transpile(qc, SIM)
        job = SIM.run(transpiled, shots=1)
        result = job.result()
        counts = result.get_counts()
        measured_bit = int(list(counts.keys())[0])  # '0' or '1'
        eve_outcome = measured_bit

        # Create new circuit to re-prepare the qubit as Eve sends it onward
        qc = QuantumCircuit(q, c)
        # Reprepare according to eve_outcome in computational basis then convert to her basis if needed
        if eve_outcome == 1:
            qc.x(q[0])
        if eve_basis == 'x':
            qc.h(q[0])
    # Now Bob measures in his basis
    if bob_basis == 'x':
        qc.h(q[0])
    qc.measure(q[0], c[0])
    transpiled = transpile(qc, SIM)
    job = SIM.run(transpiled, shots=1)
    result = job.result()
    counts = result.get_counts()
    bob_result = int(list(counts.keys())[0])
    return bob_result, eve_outcome

def run_bb84(length: int = 16, with_eve: bool = False) -> Dict:
    """
    Run BB84 protocol simulation with Qiskit simulator.

    Returns a dict containing:
      - alice_bits, alice_bases
      - bob_bases, bob_results
      - (if with_eve) eve_bases, eve_results
      - alice_key, bob_key, qber
    """
    # Generate Alice and Bob choices
    alice_bits, alice_bases = random_bits_and_bases(length)
    bob_bases = [secrets.choice(['+', 'x']) for _ in range(length)]
    eve_bases = None
    eve_results = None

    bob_results = []
    eve_results = [] if with_eve else None

    for i in range(length):
        eve_basis = None
        if with_eve:
            # Eve chooses a random basis per qubit
            eve_basis = secrets.choice(['+', 'x'])
        bob_res, eve_res = _prepare_and_measure_single_qubit(
            alice_bit=alice_bits[i],
            alice_basis=alice_bases[i],
            bob_basis=bob_bases[i],
            eve_basis=eve_basis
        )
        bob_results.append(bob_res)
        if with_eve:
            eve_results.append(eve_res)
    # Sift keys (keep bits where bases matched)
    alice_key = []
    bob_key = []
    matching_indices = []
    for i, (a_b, a_basis, b_res, b_basis) in enumerate(zip(alice_bits, alice_bases, bob_results, bob_bases)):
        if a_basis == b_basis:
            alice_key.append(a_b)
            bob_key.append(b_res)
            matching_indices.append(i)

    # Compute QBER (if there are matched bits)
    from .qber import calculate_qber
    qber = calculate_qber(alice_key, bob_key)

    resp = {
        "length": length,
        "alice_bits": alice_bits,
        "alice_bases": alice_bases,
        "bob_bases": bob_bases,
        "bob_results": bob_results,
        "matching_indices": matching_indices,
        "alice_key": alice_key,
        "bob_key": bob_key,
        "qber": qber,
    }
    if with_eve:
        resp["eve_bases"] = eve_bases if eve_bases is not None else "see per-qubit (we returned eve_results)"
        resp["eve_results"] = eve_results
    return resp

