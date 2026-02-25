package um.frontend.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import um.frontend.data.model.SeatDto
import um.frontend.ui.components.SeatMap
import um.frontend.ui.viewmodel.EventsViewModel
import um.frontend.ui.viewmodel.SelectionViewModel

@Composable
fun SeatSelectionScreen(
    eventId: Long,
    eventsVM: EventsViewModel,
    selectionVM: SelectionViewModel,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val seatMap by eventsVM.seatMap.collectAsState()
    val sel by selectionVM.selection.collectAsState()
    var selected by remember { mutableStateOf<List<SeatDto>>(emptyList()) }
    var ready by remember { mutableStateOf(false) }
    var snackbar by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(eventId) {
        ready = selectionVM.startSelectionAwait(eventId) != null
        eventsVM.loadSeatMap(eventId)
    }

    fun toggleSeat(seat: SeatDto) {
        selected = if (selected.any { it.fila == seat.fila && it.columna == seat.columna }) {
            selected.filterNot { it.fila == seat.fila && it.columna == seat.columna }
        } else {
            if (selected.size < 4) selected + seat else selected
        }
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Seleccionar asientos", style = MaterialTheme.typography.titleMedium)
        sel?.stage?.let { Text("Estado: $it") }
        sel?.bloqueadoHasta?.let { Text("Bloqueado hasta: $it") }
        Spacer(Modifier.height(8.dp))

        val rows = seatMap?.asientos?.maxOfOrNull { it.fila } ?: 10
        val cols = seatMap?.asientos?.maxOfOrNull { it.columna } ?: 10

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            SeatMap(
                rows = rows,
                cols = cols,
                seatEntries = seatMap?.asientos,
                selectedSeats = selected,
                onToggleSeat = ::toggleSeat
            )
        }

        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    scope.launch {
                        selectionVM.setSeats(selected)
                        snackbar = "Selección guardada (${selected.size})"
                    }
                },
                enabled = ready && selected.isNotEmpty(),
                modifier = Modifier.weight(1f)
            ) { Text("Guardar selección") }

            Button(
                onClick = {
                    scope.launch {
                        selectionVM.setSeats(selected)
                        val ok = selectionVM.blockAwait()
                        if (ok) onNext() else snackbar = "No se pudo bloquear. Intentá de nuevo."
                    }
                },
                enabled = ready && selected.isNotEmpty(),
                modifier = Modifier.weight(1f)
            ) { Text("Bloquear y continuar") }
        }

        snackbar?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.primary)
        }
    }
}