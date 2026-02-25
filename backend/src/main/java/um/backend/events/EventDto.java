package um.backend.events;

import java.time.Instant;
import java.util.List;

public class EventDto {
    public String titulo;
    public String resumen;
    public String descripcion;
    public Instant fecha;
    public String direccion;
    public String imagen;
    public int filaAsientos;
    public int columnAsientos;
    public double precioEntrada;
    public Tipo eventoTipo;
    public List<Integrante> integrantes;
    public long id;

    public static class Tipo {
        public String nombre;
        public String descripcion;
    }

    public static class Integrante {
        public String nombre;
        public String apellido;
        public String identificacion;
    }
}