package kh.edu.istad.platform.customer.security;

import io.netty.handler.codec.http.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http
    ) {
        return http
                .csrf(csrf -> csrf.disable())

                .authorizeExchange(exchange -> exchange
                        .pathMatchers(
                                String.valueOf(HttpMethod.GET),
                                "/api/customers",
                                "/api/customers/**"
                        )
                        .hasAuthority("SCOPE_customer.read")

                        .anyExchange().authenticated()
                )

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(
                                new HttpStatusServerEntryPoint(
                                        HttpStatus.UNAUTHORIZED
                                )
                        )
                )

                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> {})
                )
                .build();
    }
}