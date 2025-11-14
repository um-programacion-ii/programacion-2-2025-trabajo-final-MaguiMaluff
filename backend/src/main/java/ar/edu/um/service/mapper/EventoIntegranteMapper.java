package ar.edu.um.service.mapper;

import ar.edu.um.domain.Evento;
import ar.edu.um.domain.EventoIntegrante;
import ar.edu.um.service.dto.EventoDTO;
import ar.edu.um.service.dto.EventoIntegranteDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link EventoIntegrante} and its DTO {@link EventoIntegranteDTO}.
 */
@Mapper(componentModel = "spring")
public interface EventoIntegranteMapper extends EntityMapper<EventoIntegranteDTO, EventoIntegrante> {
    @Mapping(target = "evento", source = "evento", qualifiedByName = "eventoId")
    EventoIntegranteDTO toDto(EventoIntegrante s);

    @Named("eventoId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EventoDTO toDtoEventoId(Evento evento);
}
