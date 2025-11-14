package ar.edu.um.domain;

import static ar.edu.um.domain.EventoIntegranteTestSamples.*;
import static ar.edu.um.domain.EventoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EventoIntegranteTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventoIntegrante.class);
        EventoIntegrante eventoIntegrante1 = getEventoIntegranteSample1();
        EventoIntegrante eventoIntegrante2 = new EventoIntegrante();
        assertThat(eventoIntegrante1).isNotEqualTo(eventoIntegrante2);

        eventoIntegrante2.setId(eventoIntegrante1.getId());
        assertThat(eventoIntegrante1).isEqualTo(eventoIntegrante2);

        eventoIntegrante2 = getEventoIntegranteSample2();
        assertThat(eventoIntegrante1).isNotEqualTo(eventoIntegrante2);
    }

    @Test
    void eventoTest() {
        EventoIntegrante eventoIntegrante = getEventoIntegranteRandomSampleGenerator();
        Evento eventoBack = getEventoRandomSampleGenerator();

        eventoIntegrante.setEvento(eventoBack);
        assertThat(eventoIntegrante.getEvento()).isEqualTo(eventoBack);

        eventoIntegrante.evento(null);
        assertThat(eventoIntegrante.getEvento()).isNull();
    }
}
