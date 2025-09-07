package com.example.qryptalk.network

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.url
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration.Companion.seconds



@Serializable
data class WSEnvelope(
    val type: String,
    val from: String? = null,
    val to: String? = null,
    val content: String? = null, // encrypted or plain
    val timestamp: Long? = null,
    val qber: Double? = null,
    val note: String? = null
)

class ChatWebSocketManager(private val baseWsUrl: String) {
    private val client = HttpClient(OkHttp) {
        install(WebSockets)
        install(Logging) { level = LogLevel.INFO }
    }
    private val json = Json { ignoreUnknownKeys = true }

    private var session: WebSocketSession? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val connecting = AtomicBoolean(false)

    private val _incoming = MutableSharedFlow<WSEnvelope>(extraBufferCapacity = 64)
    val incoming = _incoming.asSharedFlow()

    private val _connected = MutableSharedFlow<Boolean>(replay = 1)
    val connected = _connected.asSharedFlow()

    /**
     * Start connecting. username is appended to url: e.g. ws://192.168.1.2:8000/ws/{username}
     */
    fun start(username: String) {
        if (connecting.getAndSet(true)) return
        scope.launch {
            var backoff = 1
            while (isActive) {
                try {
                    val url = if (baseWsUrl.endsWith("/")) "$baseWsUrl$username" else "$baseWsUrl/$username"
                    session = client.webSocketSession { url(url) }
                    _connected.emit(true)

                    // Listen incoming frames
                    session?.let { s ->
                        for (frame in s.incoming) {
                            if (frame is Frame.Text) {
                                val text = frame.readText()
                                try {
                                    val env = json.decodeFromString<WSEnvelope>(text)
                                    _incoming.emit(env)
                                } catch (e: Exception) {
                                    // ignore parse errors
                                }
                            }
                        }
                    }
                    _connected.emit(false)
                } catch (t: Throwable) {
                    _connected.emit(false)
                }

                // reconnect with backoff
                delay((backoff).seconds)
                backoff = (backoff * 2).coerceAtMost(16)
            }
        }
    }

    suspend fun sendEnvelope(envelope: WSEnvelope) {
        session?.send(Frame.Text(json.encodeToString(envelope)))
    }

    fun sendEnvelopeAsync(envelope: WSEnvelope) {
        scope.launch {
            try {
                sendEnvelope(envelope)
            } catch (_: Throwable) { /* ignore */ }
        }
    }

    fun stop() {
        scope.launch {
            try { session?.close() } catch (_: Throwable) {}
            try { client.close() } catch (_: Throwable) {}
            connecting.set(false)
            cancel()
        }
    }
}
