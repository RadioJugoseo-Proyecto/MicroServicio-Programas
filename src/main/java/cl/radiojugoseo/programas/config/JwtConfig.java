package cl.radiojugoseo.programas.config;

import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

// Este microservicio solo VALIDA los tokens que emite el de usuarios,
// por eso debe usar el mismo JWT_SECRET.
@Configuration
public class JwtConfig {

    @Bean
    public JwtDecoder jwtDecoder(
            @Value("${JWT_SECRET}") String claveBase64) {

        byte[] bytes;

        try {
            bytes = Base64.getDecoder().decode(claveBase64);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(
                    "JWT_SECRET debe estar codificada en Base64", ex);
        }

        if (bytes.length < 32) {
            throw new IllegalStateException(
                    "JWT_SECRET debe contener al menos 32 bytes");
        }

        SecretKey clave = new SecretKeySpec(bytes, "HmacSHA256");

        return NimbusJwtDecoder
                .withSecretKey(clave)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}
