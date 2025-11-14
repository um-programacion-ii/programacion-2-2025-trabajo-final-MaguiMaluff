package ar.edu.um.service.mapper;

import ar.edu.um.domain.Evento;
import ar.edu.um.domain.Venta;
import ar.edu.um.service.dto.EventoDTO;
import ar.edu.um.service.dto.VentaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Venta} and its DTO {@link VentaDTO}.
 */
@Mapper(componentModel = "spring")
public interface VentaMapper extends EntityMapper<VentaDTO, Venta> {
    @Mapping(target = "evento", source = "evento", qualifiedByName = "eventoId")
    VentaDTO toDto(Venta s);

    @Named("eventoId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EventoDTO toDtoEventoId(Evento evento);
}
