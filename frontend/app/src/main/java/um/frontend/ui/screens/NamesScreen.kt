package um.frontend.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateListOf
import um.frontend.data.model.SeatDto
import um.frontend.ui.viewmodel.SelectionViewModel

@Composable
fun NamesScreen(
    eventId: Long,
    selectionVM: SelectionViewModel,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val selection by selectionVM.selection.collectAsState()
    val error by selectionVM.error.collectAsState()

    LaunchedEffect(eventId) { selectionVM.startSelection(eventId) }

    val seats: List<SeatDto> = selection?.seats ?: emptyList()
    val names = remember(seats) { mutableStateListOf<String>().apply { repeat(seats.size) { add("") } } }

    Column(modifier.padding(16.dp)) {
        Text("Datos de las personas", style = MaterialTheme.typography.titleMedium)
        selection?.let { sel ->
            Text("UserId: ${sel.userId}")
            Text("SelectionId: ${sel.id}")
        }
        Spacer(Modifier.height(12.dp))

        if (seats.isEmpty()) {
            Text("No hay asientos seleccionados. Volvé atrás y guarda tu selección.", color = MaterialTheme.colorScheme.error)
        } else {
            Card {
                Column(Modifier.padding(16.dp)) {
                    Text("Cargar nombres", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(8.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(seats.indices.toList()) { idx ->
                            OutlinedTextField(
                                value = names[idx],
                                onValueChange = { names[idx] = it },
                                label = { Text("Asiento ${seats[idx].fila}-${seats[idx].columna}") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            val allFilled = names.all { it.isNotBlank() } && names.size == seats.size
            Button(
                onClick = {
                    scope.launch {
                        val ok = selectionVM.setNamesAwait(names.toList())
                        if (ok) onNext()
                    }
                },
                enabled = allFilled
            ) { Text("Continuar a compra") }

            error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}