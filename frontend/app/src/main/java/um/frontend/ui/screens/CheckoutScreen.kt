package um.frontend.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import um.frontend.ui.viewmodel.SelectionViewModel

@Composable
fun CheckoutScreen(
    eventId: Long,
    selectionVM: SelectionViewModel,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val loading by selectionVM.loading.collectAsState()
    val error by selectionVM.error.collectAsState()
    val selection by selectionVM.selection.collectAsState()

    LaunchedEffect(eventId) { selectionVM.ensureSelection(eventId) }

    val canConfirm = selectionVM.isBlockedValid() && selectionVM.hasSeatsAndNames()

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Confirmación de compra", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))

        selection?.let { sel ->
            Text("UserId: ${sel.userId}")
            Text("SelectionId: ${sel.id}")
            Text("Evento: ${sel.eventoId}")
            Text("Asientos: ${sel.seats.size}")
            Text("Nombres cargados: ${sel.names.size}")
            sel.bloqueadoHasta?.let { Text("Bloqueado hasta: $it") }
            sel.stage?.let { Text("Stage: $it") }
        }

        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                scope.launch {
                    val ok = selectionVM.confirmAwait()
                    if (ok) onDone()
                }
            },
            enabled = canConfirm && !loading
        ) { Text(if (loading) "Confirmando..." else "Confirmar") }

        Spacer(Modifier.height(12.dp))
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}