package ar.edu.um.service.mapper;

import static ar.edu.um.domain.VentaAsientoAsserts.*;
import static ar.edu.um.domain.VentaAsientoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VentaAsientoMapperTest {

    private VentaAsientoMapper ventaAsientoMapper;

    @BeforeEach
    void setUp() {
        ventaAsientoMapper = new VentaAsientoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getVentaAsientoSample1();
        var actual = ventaAsientoMapper.toEntity(ventaAsientoMapper.toDto(expected));
        assertVentaAsientoAllPropertiesEquals(expected, actual);
    }
}
