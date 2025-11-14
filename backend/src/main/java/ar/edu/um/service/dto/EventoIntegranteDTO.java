package ar.edu.um.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link ar.edu.um.domain.EventoIntegrante} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EventoIntegranteDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 120)
    private String nombre;

    @NotNull
    @Size(max = 120)
    private String apellido;

    @Size(max = 60)
    private String identificacion;

    @NotNull
    private EventoDTO evento;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public EventoDTO getEvento() {
        return evento;
    }

    public void setEvento(EventoDTO evento) {
        this.evento = evento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventoIntegranteDTO)) {
            return false;
        }

        EventoIntegranteDTO eventoIntegranteDTO = (EventoIntegranteDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, eventoIntegranteDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EventoIntegranteDTO{" +
            "id=" + getId() +
            ", nombre='" + getNombre() + "'" +
            ", apellido='" + getApellido() + "'" +
            ", identificacion='" + getIdentificacion() + "'" +
            ", evento=" + getEvento() +
            "}";
    }
}
