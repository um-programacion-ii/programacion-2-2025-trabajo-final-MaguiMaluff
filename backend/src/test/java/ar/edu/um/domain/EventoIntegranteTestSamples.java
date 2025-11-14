package ar.edu.um.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EventoIntegranteTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static EventoIntegrante getEventoIntegranteSample1() {
        return new EventoIntegrante().id(1L).nombre("nombre1").apellido("apellido1").identificacion("identificacion1");
    }

    public static EventoIntegrante getEventoIntegranteSample2() {
        return new EventoIntegrante().id(2L).nombre("nombre2").apellido("apellido2").identificacion("identificacion2");
    }

    public static EventoIntegrante getEventoIntegranteRandomSampleGenerator() {
        return new EventoIntegrante()
            .id(longCount.incrementAndGet())
            .nombre(UUID.randomUUID().toString())
            .apellido(UUID.randomUUID().toString())
            .identificacion(UUID.randomUUID().toString());
    }
}
