from fastapi import APIRouter, WebSocket, WebSocketDisconnect
from typing import Dict

router = APIRouter()

active_connections: Dict[str, WebSocket] = {}

async def connect(websocket: WebSocket, username: str):
    await websocket.accept()
    active_connections[username] = websocket

async def disconnect(username: str):
    active_connections.pop(username, None)

async def broadcast(message: str):
    for connection in active_connections.values():
        await connection.send_text(message)

@router.websocket("/ws/{username}")
async def websocket_endpoint(websocket: WebSocket, username: str):
    await connect(websocket, username)
    try:
        while True:
            data = await websocket.receive_text()
            await broadcast(f"{username}: {data}")
    except WebSocketDisconnect:
        await disconnect(username)
        await broadcast(f"{username} left the chat")
