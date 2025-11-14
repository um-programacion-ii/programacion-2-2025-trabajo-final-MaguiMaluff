package ar.edu.um.domain;

import static ar.edu.um.domain.EventoIntegranteTestSamples.*;
import static ar.edu.um.domain.EventoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class EventoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Evento.class);
        Evento evento1 = getEventoSample1();
        Evento evento2 = new Evento();
        assertThat(evento1).isNotEqualTo(evento2);

        evento2.setId(evento1.getId());
        assertThat(evento1).isEqualTo(evento2);

        evento2 = getEventoSample2();
        assertThat(evento1).isNotEqualTo(evento2);
    }

    @Test
    void integrantesTest() {
        Evento evento = getEventoRandomSampleGenerator();
        EventoIntegrante eventoIntegranteBack = getEventoIntegranteRandomSampleGenerator();

        evento.addIntegrantes(eventoIntegranteBack);
        assertThat(evento.getIntegrantes()).containsOnly(eventoIntegranteBack);
        assertThat(eventoIntegranteBack.getEvento()).isEqualTo(evento);

        evento.removeIntegrantes(eventoIntegranteBack);
        assertThat(evento.getIntegrantes()).doesNotContain(eventoIntegranteBack);
        assertThat(eventoIntegranteBack.getEvento()).isNull();

        evento.integrantes(new HashSet<>(Set.of(eventoIntegranteBack)));
        assertThat(evento.getIntegrantes()).containsOnly(eventoIntegranteBack);
        assertThat(eventoIntegranteBack.getEvento()).isEqualTo(evento);

        evento.setIntegrantes(new HashSet<>());
        assertThat(evento.getIntegrantes()).doesNotContain(eventoIntegranteBack);
        assertThat(eventoIntegranteBack.getEvento()).isNull();
    }
}
