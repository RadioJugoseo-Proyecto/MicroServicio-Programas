package cl.radiojugoseo.programas.config;

import jakarta.servlet.DispatcherType;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        // Interpretar el campo "rol" del JWT (igual que en usuarios)
        JwtGrantedAuthoritiesConverter roles =
                new JwtGrantedAuthoritiesConverter();
        roles.setAuthoritiesClaimName("rol");
        roles.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter convertidor =
                new JwtAuthenticationConverter();
        convertidor.setJwtGrantedAuthoritiesConverter(roles);

        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)

            .authorizeHttpRequests(auth -> auth

                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()

                // La parrilla y las transmisiones son públicas (el sitio las muestra)
                .requestMatchers(HttpMethod.GET,
                    "/api/v1/programas/**",
                    "/api/v1/transmisiones/**"
                ).permitAll()

                // Iniciar / finalizar transmisión: Locutor o Administrador
                .requestMatchers(HttpMethod.POST,
                    "/api/v1/programas/{id}/transmisiones",
                    "/api/v1/transmisiones/{id}/finalizar"
                ).hasAnyRole("Locutor", "Administrador")

                // CRUD de programas: solo Administrador
                .requestMatchers(HttpMethod.POST, "/api/v1/programas")
                    .hasRole("Administrador")
                .requestMatchers(HttpMethod.PUT, "/api/v1/programas/{id}")
                    .hasRole("Administrador")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/programas/{id}")
                    .hasRole("Administrador")

                .anyRequest().authenticated()
            )

            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(convertidor)));

        return http.build();
    }
}
