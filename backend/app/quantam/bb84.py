# app/quantum/bb84.py
import random

def generate_qubits(length: int = 16):
    bits = [random.randint(0, 1) for _ in range(length)]
    bases = [random.choice(['+', 'x']) for _ in range(length)]  # + = rectilinear, x = diagonal
    return bits, bases

def measure_qubits(bits, bases, bob_bases):
    results = []
    for bit, a_basis, b_basis in zip(bits, bases, bob_bases):
        if a_basis == b_basis:
            results.append(bit)  # correct measurement
        else:
            results.append(random.randint(0, 1))  # random outcome
    return results

def sift_key(alice_bits, alice_bases, bob_results, bob_bases):
    key = []
    for a_bit, a_basis, b_bit, b_basis in zip(alice_bits, alice_bases, bob_results, bob_bases):
        if a_basis == b_basis:
            key.append(a_bit)
    return key
