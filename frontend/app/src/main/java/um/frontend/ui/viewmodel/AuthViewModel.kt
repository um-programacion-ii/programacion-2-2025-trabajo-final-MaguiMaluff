package um.frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import um.frontend.data.model.*
import um.frontend.data.repo.AuthRepository
import um.frontend.data.repo.EventsRepository
import um.frontend.data.repo.SelectionRepository
import um.frontend.data.store.TokenStore
import java.time.Instant
import java.time.format.DateTimeParseException

class AuthViewModel(
    private val authRepo: AuthRepository,
    private val tokenStore: TokenStore
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.LoggedOut)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(username: String, password: String) = viewModelScope.launch {
        runCatching { authRepo.login(username, password) }
            .onSuccess { resp ->
                val token = resp.id_token ?: resp.idToken ?: resp.token
                if (token.isNullOrBlank()) {
                    _authState.value = AuthState.Error("Login sin token")
                    return@onSuccess
                }
                val userId = resp.userId ?: username
                _authState.value = AuthState.LoggedIn(token, userId)
                tokenStore.saveToken(token)
                tokenStore.saveUserId(userId)
            }
            .onFailure { _authState.value = AuthState.Error(it.message ?: "Error de login") }
    }

    suspend fun signupAwait(username: String, password: String, userId: String): Result<SignupResponse> =
        runCatching { authRepo.signup(username, password, userId) }

    fun logout() = viewModelScope.launch {
        _authState.value = AuthState.LoggedOut
        tokenStore.saveToken(null); tokenStore.saveUserId(null)
    }
}
sealed class AuthState {
    data object LoggedOut : AuthState()
    data class LoggedIn(val token: String, val userId: String?) : AuthState()
    data class Error(val message: String) : AuthState()
}

class EventsViewModel(private val repo: EventsRepository) : ViewModel() {
    private val _events = MutableStateFlow<List<EventSummaryDto>>(emptyList())
    val events: StateFlow<List<EventSummaryDto>> = _events.asStateFlow()
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _seatStates = MutableStateFlow<List<SeatStateEntryDto>>(emptyList())
    val seatStates: StateFlow<List<SeatStateEntryDto>> = _seatStates.asStateFlow()

    fun loadEvents() = viewModelScope.launch {
        _loading.value = true; _error.value = null
        runCatching { repo.listEvents() }.onSuccess { _events.value = it }.onFailure { _error.value = it.message }
        _loading.value = false
    }

    private val _seatMap = MutableStateFlow<SeatMapDto?>(null)
    val seatMap: StateFlow<SeatMapDto?> = _seatMap.asStateFlow()

    fun loadSeatMap(eventId: Long) = viewModelScope.launch {
        runCatching { repo.getSeatMap(eventId) }.onSuccess { _seatMap.value = it }
    }

    suspend fun getEvent(id: Long): EventDetailDto = repo.getEvent(id)
}
class SelectionViewModel(
    private val repo: SelectionRepository,
    private val tokenStore: TokenStore
) : ViewModel() {

    private val _selection = MutableStateFlow<SelectionResponseDto?>(null)
    val selection: StateFlow<SelectionResponseDto?> = _selection.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    suspend fun startSelectionAwait(eventoId: Long): SelectionResponseDto? {
        val userId = tokenStore.getUserId()
        if (userId.isNullOrBlank()) {
            _error.value = "No hay usuario en sesión. Iniciá sesión nuevamente."
            return null
        }
        _error.value = null
        return runCatching { repo.createSelection(userId, eventoId) }
            .onSuccess { _selection.value = it }
            .onFailure { _error.value = it.message }
            .getOrNull()
    }
    fun startSelection(eventoId: Long) = viewModelScope.launch { startSelectionAwait(eventoId) }

    fun setSeats(seats: List<SeatDto>) = viewModelScope.launch {
        val sel = _selection.value ?: return@launch
        _error.value = null
        runCatching { repo.setSeats(sel.id, seats) }
            .onSuccess { _selection.value = it }     // debe traer seats persistidos
            .onFailure { _error.value = it.message }
    }

    suspend fun setNamesAwait(names: List<String>): Boolean {
        val sel = _selection.value ?: return false
        _error.value = null
        return runCatching { repo.setNames(sel.id, names) }
            .onSuccess { _selection.value = it }
            .onFailure { _error.value = it.message }
            .isSuccess
    }

    suspend fun blockAwait(): Boolean {
        val sel = _selection.value ?: return false
        _error.value = null
        return runCatching { repo.block(sel.id) }
            .onSuccess { _selection.value = it }
            .onFailure { _error.value = it.message }
            .isSuccess
    }

    fun confirm() = viewModelScope.launch {
        val sel = _selection.value ?: run { _error.value = "No hay selección activa"; return@launch }
        _loading.value = true; _error.value = null
        runCatching { repo.confirm(sel.id) }
            .onFailure { _error.value = it.message }
        _loading.value = false
    }

    fun isBlockedValid(now: Instant = Instant.now()): Boolean {
        val ts = _selection.value?.bloqueadoHasta ?: return false
        return try { Instant.parse(ts).isAfter(now) } catch (_: DateTimeParseException) { false }
    }

    fun hasSeatsAndNames(): Boolean {
        val sel = _selection.value ?: return false
        return sel.seats.isNotEmpty() && sel.names.size == sel.seats.size
    }
}