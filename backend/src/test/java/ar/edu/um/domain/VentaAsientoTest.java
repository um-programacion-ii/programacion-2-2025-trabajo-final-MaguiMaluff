package ar.edu.um.domain;

import static ar.edu.um.domain.VentaAsientoTestSamples.*;
import static ar.edu.um.domain.VentaTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.um.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VentaAsientoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(VentaAsiento.class);
        VentaAsiento ventaAsiento1 = getVentaAsientoSample1();
        VentaAsiento ventaAsiento2 = new VentaAsiento();
        assertThat(ventaAsiento1).isNotEqualTo(ventaAsiento2);

        ventaAsiento2.setId(ventaAsiento1.getId());
        assertThat(ventaAsiento1).isEqualTo(ventaAsiento2);

        ventaAsiento2 = getVentaAsientoSample2();
        assertThat(ventaAsiento1).isNotEqualTo(ventaAsiento2);
    }

    @Test
    void ventaTest() {
        VentaAsiento ventaAsiento = getVentaAsientoRandomSampleGenerator();
        Venta ventaBack = getVentaRandomSampleGenerator();

        ventaAsiento.setVenta(ventaBack);
        assertThat(ventaAsiento.getVenta()).isEqualTo(ventaBack);

        ventaAsiento.venta(null);
        assertThat(ventaAsiento.getVenta()).isNull();
    }
}
