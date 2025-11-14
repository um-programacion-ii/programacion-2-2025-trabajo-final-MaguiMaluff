package ar.edu.um.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EventoIntegranteDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventoIntegranteDTO.class);
        EventoIntegranteDTO eventoIntegranteDTO1 = new EventoIntegranteDTO();
        eventoIntegranteDTO1.setId(1L);
        EventoIntegranteDTO eventoIntegranteDTO2 = new EventoIntegranteDTO();
        assertThat(eventoIntegranteDTO1).isNotEqualTo(eventoIntegranteDTO2);
        eventoIntegranteDTO2.setId(eventoIntegranteDTO1.getId());
        assertThat(eventoIntegranteDTO1).isEqualTo(eventoIntegranteDTO2);
        eventoIntegranteDTO2.setId(2L);
        assertThat(eventoIntegranteDTO1).isNotEqualTo(eventoIntegranteDTO2);
        eventoIntegranteDTO1.setId(null);
        assertThat(eventoIntegranteDTO1).isNotEqualTo(eventoIntegranteDTO2);
    }
}
