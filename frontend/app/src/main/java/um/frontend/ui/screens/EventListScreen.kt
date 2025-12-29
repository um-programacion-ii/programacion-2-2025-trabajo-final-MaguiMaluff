package um.frontend.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import um.frontend.data.model.EventSummaryDto
import um.frontend.ui.viewmodel.EventsViewModel

@Composable
fun EventListScreen(
    eventsVM: EventsViewModel,
    onSelectEvent: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val events by eventsVM.events.collectAsState()
    val loading by eventsVM.loading.collectAsState()
    val error by eventsVM.error.collectAsState()

    LaunchedEffect(Unit) { eventsVM.loadEvents() }

    Column(modifier.fillMaxSize().padding(16.dp)) {
        if (loading) LinearProgressIndicator(modifier = Modifier.fillMaxSize())
        if (error != null) Text(error!!, color = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(8.dp))
        LazyColumn {
            items(events) { e ->
                EventCard(e) { onSelectEvent(e.id) }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun EventCard(event: EventSummaryDto, onClick: () -> Unit) {
    Card(onClick = onClick) {
        Column(Modifier.padding(12.dp)) {
            Text(event.titulo, style = MaterialTheme.typography.titleMedium)
            event.resumen?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
            Text("Fecha: ${event.fecha}", style = MaterialTheme.typography.bodySmall)
            event.precioEntrada?.let { Text("Precio: $it", style = MaterialTheme.typography.bodySmall) }
        }
    }
}