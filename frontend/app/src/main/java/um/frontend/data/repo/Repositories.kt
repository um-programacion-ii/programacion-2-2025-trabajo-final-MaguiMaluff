package um.frontend.data.repo

import um.frontend.data.api.BackendApi
import um.frontend.data.model.*

class AuthRepository(private val api: BackendApi) {
    suspend fun signup(username: String, password: String, userId: String): SignupResponse =
        api.signup(SignupRequest(username, password, userId))
    suspend fun login(username: String, password: String): LoginResponse =
        api.login(LoginRequest(username, password))
}

class EventsRepository(private val api: BackendApi) {
    suspend fun listEvents(): List<EventSummaryDto> = api.listEvents()
    suspend fun getEvent(id: Long): EventDetailDto = api.getEvent(id)
    suspend fun getSeatMap(eventId: Long): SeatMapDto = api.getSeatMap(eventId)
}

class SelectionRepository(private val api: BackendApi) {
    suspend fun createSelection(userId: String, eventoId: Long): SelectionResponseDto =
        api.createSelection(userId, eventoId)
    suspend fun setSeats(selectionId: String, seats: List<SeatDto>): SelectionResponseDto =
        api.setSeats(selectionId, seats)
    suspend fun setNames(selectionId: String, names: List<String>): SelectionResponseDto =
        api.setNames(selectionId, names)
    suspend fun block(selectionId: String): SelectionResponseDto = api.block(selectionId)
    suspend fun confirm(selectionId: String) = api.confirm(selectionId)
}