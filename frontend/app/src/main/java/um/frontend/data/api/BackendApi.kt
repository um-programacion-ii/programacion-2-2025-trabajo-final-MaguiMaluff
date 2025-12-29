package um.frontend.data.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import um.frontend.AppConfig
import um.frontend.data.model.*
import java.util.concurrent.TimeUnit

class BackendApi(private val tokenProvider: () -> String?) {
    private val client = HttpClient(OkHttp) {
        engine { config { connectTimeout(10, TimeUnit.SECONDS); readTimeout(20, TimeUnit.SECONDS); writeTimeout(20, TimeUnit.SECONDS) } }
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true; isLenient = true }) }
    }

    private fun auth(h: io.ktor.client.request.HttpRequestBuilder) {
        h.header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        h.header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        tokenProvider()?.let { h.header(HttpHeaders.Authorization, "Bearer $it") }
    }

    // Auth
    suspend fun signup(req: SignupRequest): SignupResponse =
        client.post("${AppConfig.baseUrl}/api/auth/signup") { auth(this); setBody(req) }.body()

    suspend fun login(req: LoginRequest): LoginResponse {
        val resp = client.post("${AppConfig.baseUrl}/api/auth/login") { auth(this); setBody(req) }
        val text = resp.bodyAsText()
        if (!resp.status.isSuccess()) throw Exception("Login failed: http=${resp.status.value} body=$text")
        val obj = Json.parseToJsonElement(text).jsonObject
        val token = obj["id_token"]?.jsonPrimitive?.contentOrNull
            ?: obj["idToken"]?.jsonPrimitive?.contentOrNull
            ?: obj["token"]?.jsonPrimitive?.contentOrNull
        val sessionId = obj["sessionId"]?.jsonPrimitive?.contentOrNull
        val userId = obj["userId"]?.jsonPrimitive?.contentOrNull
        return LoginResponse(id_token = token, sessionId = sessionId, userId = userId)
    }

    // Eventos
    suspend fun listEvents(): List<EventSummaryDto> =
        client.get("${AppConfig.baseUrl}/api/events") { auth(this) }.body()

    suspend fun getEvent(id: Long): EventDetailDto =
        client.get("${AppConfig.baseUrl}/api/events/$id") { auth(this) }.body()

    // ESTADOS de asientos del evento
    suspend fun getEventSeats(eventId: Long): List<SeatStateEntryDto> =
        client.get("${AppConfig.baseUrl}/api/events/$eventId/seats") { auth(this) }.body()

    // Selección
    suspend fun createSelection(userId: String, eventoId: Long): SelectionResponseDto =
        client.post("${AppConfig.baseUrl}/api/selection") { auth(this); setBody(CreateSelectionRequest(userId, eventoId)) }.body()

    suspend fun setSeats(selectionId: String, seats: List<SeatDto>): SelectionResponseDto =
        client.post("${AppConfig.baseUrl}/api/selection/seats") { auth(this); setBody(SeatsRequest(selectionId, seats)) }.body()

    suspend fun setNames(selectionId: String, names: List<String>): SelectionResponseDto =
        client.post("${AppConfig.baseUrl}/api/selection/names") { auth(this); setBody(NamesRequest(selectionId, names)) }.body()

    suspend fun block(selectionId: String): SelectionResponseDto =
        client.post("${AppConfig.baseUrl}/api/selection/block") { auth(this); setBody(BlockRequest(selectionId)) }.body()

    suspend fun confirm(selectionId: String): ConfirmResponseDto {
        val resp = client.post("${AppConfig.baseUrl}/api/selection/confirm") {
            auth(this)
            setBody(ConfirmRequest(selectionId))
        }
        val text = resp.bodyAsText()
        if (!resp.status.isSuccess()) {
            throw Exception("Confirmación falló: http=${resp.status.value} body=$text")
        }
        return Json.decodeFromString(text)
    }
    suspend fun getSeatMap(eventId: Long): SeatMapDto =
        client.get("${AppConfig.baseUrl}/api/events/$eventId/seats") { auth(this) }.body()

}