package ar.edu.um.proxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Clase principal: arranca la aplicación Spring Boot.
 * - @SpringBootApplication habilita component-scan, auto-configuration y bean definition.
 * - No contiene lógica de proxy en sí; solo arranca el contexto.
 */
@SpringBootApplication
public class ProxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProxyApplication.class, args);
	}

}