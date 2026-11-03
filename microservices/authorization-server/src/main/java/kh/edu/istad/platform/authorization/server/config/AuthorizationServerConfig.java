package kh.edu.istad.platform.authorization.server.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;

import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.UUID;

@Configuration
public class AuthorizationServerConfig {

    @Bean
    public RegisteredClientRepository registeredClientRepository(
            PasswordEncoder passwordEncoder
    ) {

        RegisteredClient orderService = RegisteredClient
                .withId(UUID.randomUUID().toString())
                .clientId("order-service")
                .clientSecret(
                        passwordEncoder.encode(
                                System.getenv().getOrDefault(
                                        "ORDER_SERVICE_CLIENT_SECRET",
                                        "order-service-secret"
                                )
                        )
                )
                .clientAuthenticationMethod(
                        ClientAuthenticationMethod.CLIENT_SECRET_BASIC
                )
                .authorizationGrantType(
                        AuthorizationGrantType.CLIENT_CREDENTIALS
                )
                .scope("customer.read")
                .tokenSettings(
                        TokenSettings.builder()
                                .accessTokenTimeToLive(Duration.ofMinutes(5))
                                .build()
                )
                .build();

        return new InMemoryRegisteredClientRepository(orderService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories
                .createDelegatingPasswordEncoder();
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {

        KeyPair keyPair = generateRsaKey();

        RSAPublicKey publicKey =
                (RSAPublicKey) keyPair.getPublic();

        RSAPrivateKey privateKey =
                (RSAPrivateKey) keyPair.getPrivate();

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .algorithm(JWSAlgorithm.RS256)
                .build();

        JWKSet jwkSet = new JWKSet(rsaKey);

        return new ImmutableJWKSet<>(jwkSet);
    }

    private KeyPair generateRsaKey() {

        try {

            KeyPairGenerator generator =
                    KeyPairGenerator.getInstance("RSA");

            generator.initialize(2048);

            return generator.generateKeyPair();

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Cannot generate RSA key pair",
                    exception
            );
        }
    }

    @Bean
    public JwtDecoder jwtDecoder(
            JWKSource<SecurityContext> jwkSource
    ) {
        return OAuth2AuthorizationServerConfiguration
                .jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {

        return AuthorizationServerSettings.builder()
                .issuer("http://localhost:4321")
                .build();
    }
}
