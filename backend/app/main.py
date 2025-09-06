from fastapi import FastAPI
from app.routers import quantum, chat 

app = FastAPI()

app.include_router(quantum.router, prefix="/quantum", tags=["quantum"])
app.include_router(chat.router, prefix="/chat", tags=["chat"])


@app.get("/")
def root():
    return {"message": "QrypTalk backend running!"}
