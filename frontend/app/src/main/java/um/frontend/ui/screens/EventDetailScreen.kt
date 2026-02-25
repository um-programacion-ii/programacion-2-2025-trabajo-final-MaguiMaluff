package um.frontend.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import um.frontend.data.model.EventDetailDto
import um.frontend.ui.viewmodel.EventsViewModel

@Composable
fun EventDetailScreen(
    eventsVM: EventsViewModel,
    eventId: Long,
    onStartSelection: () -> Unit,
    modifier: Modifier = Modifier
) {
    var event by remember { mutableStateOf<EventDetailDto?>(null) }

    LaunchedEffect(eventId) {
        event = runCatching { eventsVM.getEvent(eventId) }.getOrNull()
    }

    Column(modifier.fillMaxSize().padding(16.dp)) {
        event?.let { e ->
            Text(e.titulo, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            AsyncImage(model = e.imagen, contentDescription = e.titulo)
            Spacer(Modifier.height(8.dp))
            e.descripcion?.let { Text(it) }
            Spacer(Modifier.height(8.dp))
            Text("Fecha: ${e.fecha}")
            e.direccion?.let { Text("Lugar: $it") }
            Spacer(Modifier.height(16.dp))
            Button(onClick = onStartSelection) {
                Text("Seleccionar asientos")
            }
        } ?: Text("Cargando detalle...")
    }
}