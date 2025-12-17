package um.backend.seats;

import java.time.Instant;
import java.util.List;

public class SeatMapDto {
    public long eventoId;
    public List<Seat> asientos;

    public static class Seat {
        public int fila;
        public int columna;
        public String estado; // Libre, Bloqueado, Vendido
        public Instant expira; // opcional si Bloqueado
    }
}