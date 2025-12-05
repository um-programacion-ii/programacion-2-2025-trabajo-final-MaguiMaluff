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