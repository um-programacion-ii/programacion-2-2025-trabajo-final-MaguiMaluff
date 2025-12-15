package ar.edu.um.proxy.domain;

/**
 * Simple value object para representar un ID de evento.
 */
public final class EventoId {
    private final Long id;

    public EventoId(Long id) {
        this.id = id;
    }
    public Long value() { return id; }
}