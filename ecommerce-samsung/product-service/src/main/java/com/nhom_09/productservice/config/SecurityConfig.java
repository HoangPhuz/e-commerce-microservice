package com.nhom_09.productservice.config;

import jakarta.ws.rs.HttpMethod;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final String  SECRET_KEY  = "my-very-long-and-secure-jwt-secret-key-1234567890";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // Tắt CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/products/**", "/images/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder())
                        .jwtAuthenticationConverter(jwtAuthenticationConverter())

                ))
                .build();
    }
    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }

    // Bean này đóng vai trò là chuyển roles claim từ token sang scope để OAuth2 hiểu
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        // Tạo một bộ chuyển đổi cho các quyền (authorities)
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // Chỉ định rằng nó nên tìm quyền trong claim có tên là "roles"
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        // Bỏ tiền tố "SCOPE_" mặc định mà Spring hay thêm vào
        grantedAuthoritiesConverter.setAuthorityPrefix("");

        // Tạo bộ chuyển đổi token chính
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        // Gán bộ chuyển đổi quyền hạn tùy chỉnh của chúng ta vào
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

}
