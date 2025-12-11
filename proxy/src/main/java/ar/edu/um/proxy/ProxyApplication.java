package ar.edu.um.proxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Punto de arranque de la aplicación proxy.
 * Habilita @EnableScheduling para que el servicio de autenticación pueda refrescar token periódicamente.
 */
@SpringBootApplication
@EnableScheduling
public class ProxyApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProxyApplication.class, args);
	}
}