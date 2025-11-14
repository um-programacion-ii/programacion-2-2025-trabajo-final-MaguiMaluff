package ar.edu.um.service.mapper;

import static ar.edu.um.domain.EventoIntegranteAsserts.*;
import static ar.edu.um.domain.EventoIntegranteTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventoIntegranteMapperTest {

    private EventoIntegranteMapper eventoIntegranteMapper;

    @BeforeEach
    void setUp() {
        eventoIntegranteMapper = new EventoIntegranteMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getEventoIntegranteSample1();
        var actual = eventoIntegranteMapper.toEntity(eventoIntegranteMapper.toDto(expected));
        assertEventoIntegranteAllPropertiesEquals(expected, actual);
    }
}
