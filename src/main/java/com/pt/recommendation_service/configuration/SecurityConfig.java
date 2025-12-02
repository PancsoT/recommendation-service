package com.pt.recommendation_service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration class.
 * <p>
 * Configures HTTP security for the application, including endpoint access rules and form-based login.
 * Allows unrestricted access to API documentation (Swagger UI and OpenAPI docs) and the /cryptos endpoints,
 * while requiring authentication for all other requests.
 * </p>
 */
@Configuration
public class SecurityConfig {

    /**
     * Configures the application's security filter chain.
     * <p>
     * - Permits all requests to /cryptos/**, /swagger-ui/**, and /v3/api-docs/** without authentication.<br>
     * - Requires authentication for any other request.<br>
     * - Enables form-based login with a default success URL of "/".
     * </p>
     *
     * @param http the {@link HttpSecurity} to modify
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                                "/cryptos/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .defaultSuccessUrl("/", true)
                );
        return http.build();
    }
}