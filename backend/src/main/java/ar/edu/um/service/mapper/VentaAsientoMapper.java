package ar.edu.um.service.mapper;

import ar.edu.um.domain.Venta;
import ar.edu.um.domain.VentaAsiento;
import ar.edu.um.service.dto.VentaAsientoDTO;
import ar.edu.um.service.dto.VentaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link VentaAsiento} and its DTO {@link VentaAsientoDTO}.
 */
@Mapper(componentModel = "spring")
public interface VentaAsientoMapper extends EntityMapper<VentaAsientoDTO, VentaAsiento> {
    @Mapping(target = "venta", source = "venta", qualifiedByName = "ventaId")
    VentaAsientoDTO toDto(VentaAsiento s);

    @Named("ventaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    VentaDTO toDtoVentaId(Venta venta);
}
