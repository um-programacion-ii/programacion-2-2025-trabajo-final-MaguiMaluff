package ar.edu.um.service.mapper;

import ar.edu.um.domain.Evento;
import ar.edu.um.service.dto.EventoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Evento} and its DTO {@link EventoDTO}.
 */
@Mapper(componentModel = "spring")
public interface EventoMapper extends EntityMapper<EventoDTO, Evento> {}
