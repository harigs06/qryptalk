# app/routers/chat.py
from fastapi import APIRouter, WebSocket, WebSocketDisconnect
from typing import Dict, Any
import json
from app.services.quantum_service import generate_bb84_keys

router = APIRouter()
active_connections: Dict[str, WebSocket] = {}

async def connect(websocket: WebSocket, username: str):
    await websocket.accept()
    active_connections[username] = websocket

async def disconnect(username: str):
    active_connections.pop(username, None)

async def send_to_user(username: str, message: Dict[str, Any]):
    ws = active_connections.get(username)
    if ws:
        if "from" not in message:
            message["from"] = "server"
        await ws.send_text(json.dumps(message))


@router.websocket("/ws/{username}")
async def websocket_endpoint(websocket: WebSocket, username: str):
    await connect(websocket, username)
    try:
        while True:
            text = await websocket.receive_text()
            try:
                data = json.loads(text)
            except Exception:
                # ignore invalid json
                continue

            if msg_type == "chat":
                to_user = data.get("to")
                from_user = username   # sender is the path param
                data["from"] = from_user
                if to_user:
                    await send_to_user(to_user, data)
                    await send_to_user(from_user, {**data, "status": "sent"})

                else:
                    # broadcast fallback
                    for user, conn in active_connections.items():
                        await conn.send_text(text)

            elif msg_type == "request_qber":
                # optional: compute and send QBER back
                n = int(data.get("n", 16))
                with_eve = bool(data.get("with_eve", False))
                result = generate_bb84_keys(n, with_eve)
                qber_value = result.get("qber", 0.0)
                # send qber to both users (sender and target if provided)
                # example payload:
                payload = {
                    "type": "qber",
                    "from": "server",
                    "to": username,
                    "qber": qber_value,
                    "details": result
                }
                await send_to_user(username, payload)
                # also send to target if requested
                target = data.get("target")
                if target:
                    payload2 = {**payload, "to": target}
                    await send_to_user(target, payload2)

            elif msg_type == "control":
                # handle control messages if any
                pass

            else:
                # fallback: broadcast
                for conn in active_connections.values():
                    await conn.send_text(text)

    except WebSocketDisconnect:
        await disconnect(username)
        # notify others
        for conn in active_connections.values():
            await conn.send_text(json.dumps({"type":"system","msg":f"{username} disconnected"}))
