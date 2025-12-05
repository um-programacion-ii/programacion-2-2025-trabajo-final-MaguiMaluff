package ar.edu.um.proxy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/*
 * SecurityConfig
 *
 * Configuración de seguridad Web para la aplicación.
 * Actualmente está configurada para deshabilitar CSRF y permitir todas las solicitudes.
 *
 * Importante:
 * - Con permitAll(), no hay autenticación ni autorización aplicada a las rutas.
 *   Esto puede ser aceptable en entornos cerrados, pero en producción generalmente deberías
 *   reemplazar esto por la validación de JWT/HMAC u otro mecanismo.
 * - @EnableMethodSecurity habilita anotaciones como @PreAuthorize a nivel de método.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // CSRF deshabilitado (útil para APIs REST que no usan cookies de sesión)
                .authorizeHttpRequests().anyRequest().permitAll(); // Permitir todo

        return http.build();
    }
}