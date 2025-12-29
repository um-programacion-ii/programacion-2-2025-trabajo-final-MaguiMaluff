package um.frontend.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Auth
@Serializable
data class SignupRequest(val username: String, val password: String, val userId: String)

@Serializable
data class SignupResponse(val created: Boolean? = null, val message: String? = null)

@Serializable
data class LoginRequest(val username: String, val password: String, val rememberMe: Boolean = false)

// Aceptar distintas claves posibles del token
@Serializable
data class LoginResponse(
    @SerialName("id_token") val id_token: String? = null,
    @SerialName("idToken") val idToken: String? = null,
    val token: String? = null,
    val sessionId: String? = null,
    val userId: String? = null
)

// Eventos
@Serializable
data class EventSummaryDto(val id: Long, val titulo: String, val resumen: String? = null, val descripcion: String? = null, val fecha: String, val precioEntrada: Double? = null)

@Serializable
data class EventDetailDto(val id: Long, val titulo: String, val resumen: String? = null, val descripcion: String? = null, val fecha: String, val direccion: String? = null, val imagen: String? = null, val precioEntrada: Double? = null)

// Selección
@Serializable
data class SeatDto(val fila: Int, val columna: Int)

@Serializable
enum class SeatState { Libre, Bloqueado, Ocupado, SeleccionadoPorMi }

@Serializable
data class SeatStateEntryDto(val fila: Int, val columna: Int, val estado: SeatState)

@Serializable
data class SeatMapDto(
    val eventoId: Long,
    val asientos: List<SeatEntryDto> = emptyList()
)

@Serializable
data class SeatEntryDto(
    val fila: Int,
    val columna: Int,
    val estado: String, // "Libre", "Bloqueado", "Vendido"
    val expira: String? = null
)
@Serializable
data class SelectionResponseDto(
    val id: String,               // UUID como String
    val userId: String,
    val eventoId: Long,
    val seats: List<SeatDto> = emptyList(),
    val names: List<String> = emptyList(),
    val stage: String? = null,
    val bloqueadoHasta: String? = null,
    val updatedAt: String? = null
)

// Requests según backend
@Serializable
data class CreateSelectionRequest(val userId: String, val eventoId: Long)

@Serializable
data class SeatsRequest(val selectionId: String, val seats: List<SeatDto>)

@Serializable
data class NamesRequest(val selectionId: String, val names: List<String>)

@Serializable
data class BlockRequest(val selectionId: String)

@Serializable
data class ConfirmRequest(val selectionId: String)

@Serializable
data class ConfirmItemDto(
    val fila: Int,
    val columna: Int,
    val nombre: String
)

@Serializable
data class ConfirmResponseDto(
    val saleId: String? = null,
    val externalSaleId: String? = null,
    val eventoId: Long,
    val userId: String,
    val total: Double? = null,
    val createdAt: String? = null,
    val items: List<ConfirmItemDto> = emptyList()
)