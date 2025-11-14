package ar.edu.um.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VentaAsientoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(VentaAsientoDTO.class);
        VentaAsientoDTO ventaAsientoDTO1 = new VentaAsientoDTO();
        ventaAsientoDTO1.setId(1L);
        VentaAsientoDTO ventaAsientoDTO2 = new VentaAsientoDTO();
        assertThat(ventaAsientoDTO1).isNotEqualTo(ventaAsientoDTO2);
        ventaAsientoDTO2.setId(ventaAsientoDTO1.getId());
        assertThat(ventaAsientoDTO1).isEqualTo(ventaAsientoDTO2);
        ventaAsientoDTO2.setId(2L);
        assertThat(ventaAsientoDTO1).isNotEqualTo(ventaAsientoDTO2);
        ventaAsientoDTO1.setId(null);
        assertThat(ventaAsientoDTO1).isNotEqualTo(ventaAsientoDTO2);
    }
}
