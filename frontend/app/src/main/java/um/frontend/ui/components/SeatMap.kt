package um.frontend.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import um.frontend.data.model.SeatDto
import um.frontend.data.model.SeatEntryDto

@Composable
fun SeatLegend() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        LegendDot(Color(0xFF2E7D32), "Libre")
        LegendDot(Color(0xFFFFA000), "Bloqueado")
        LegendDot(Color(0xFFD32F2F), "Vendido")
        LegendDot(Color(0xFF1E88E5), "Mi selección")
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun SeatMap(
    rows: Int,
    cols: Int,
    seatEntries: List<SeatEntryDto>?,
    selectedSeats: List<SeatDto>,
    onToggleSeat: (SeatDto) -> Unit
) {
    val stateMap = remember(seatEntries) {
        seatEntries?.associate { (it.fila to it.columna) to it.estado } ?: emptyMap()
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        SeatLegend()
        Spacer(Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(cols.coerceIn(1, 12)),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxSize() // ocupa el alto disponible (dado por Box.weight)
        ) {
            val itemsList = (1..rows).flatMap { r -> (1..cols).map { c -> SeatDto(r, c) } }
            items(itemsList) { seat ->
                val isSelected = selectedSeats.any { it.fila == seat.fila && it.columna == seat.columna }
                val estado = stateMap[seat.fila to seat.columna] ?: "Libre"
                val color = when {
                    isSelected -> Color(0xFF1E88E5)
                    estado.equals("Vendido", true) -> Color(0xFFD32F2F)
                    estado.equals("Bloqueado", true) -> Color(0xFFFFA000)
                    else -> Color(0xFF2E7D32)
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(color)
                        .clickable(enabled = !estado.equals("Vendido", true) && !estado.equals("Bloqueado", true)) {
                            onToggleSeat(seat)
                        }
                ) {
                    Text("${seat.fila}", color = Color.White, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
                }
            }
        }
    }
}