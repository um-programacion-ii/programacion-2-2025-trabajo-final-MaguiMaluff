package um.frontend.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import um.frontend.data.model.EventDetailDto
import um.frontend.ui.viewmodel.EventsViewModel
import um.frontend.ui.viewmodel.SelectionViewModel

@Composable
fun EventDetailScreen(
    eventsVM: EventsViewModel,
    selectionVM: SelectionViewModel,
    eventId: Long,
    onNavigateTo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var event by remember { mutableStateOf<EventDetailDto?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(eventId) {
        event = eventsVM.getEvent(eventId)
        eventsVM.loadSeatMap(eventId)
        selectionVM.ensureSelection(eventId)
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Detalle del evento")
        Spacer(Modifier.height(12.dp))
        event?.let {
            Text("Título: ${it.titulo}")
            AsyncImage(model = it.imagen, contentDescription = it.titulo)
            Text("Fecha: ${it.fecha}")
            Text("${it.descripcion}")
            Text("Precio: ${it.precioEntrada}")
            Text("Direccion: ${it.direccion}")

            Spacer(Modifier.height(12.dp))

            Button(onClick = {
                scope.launch {
                    selectionVM.ensureSelection(eventId)
                    val route = selectionVM.nextStepRoute(eventId)
                    onNavigateTo(route)
                }
            }) { Text("Continuar") }
        } ?: Text("Cargando...")
    }
}