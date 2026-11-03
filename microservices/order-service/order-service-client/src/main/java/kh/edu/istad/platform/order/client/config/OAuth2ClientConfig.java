package kh.edu.istad.platform.order.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.security.oauth2.client.web.client.RequestAttributePrincipalResolver;
import org.springframework.web.client.RestClient;

@Configuration
public class OAuth2ClientConfig {

    @Bean
    public RestClient customerServiceRestClient(
            RestClient.Builder builder,
            OAuth2AuthorizedClientManager authorizedClientManager
    ) {
        OAuth2ClientHttpRequestInterceptor interceptor =
                new OAuth2ClientHttpRequestInterceptor(authorizedClientManager);

        interceptor.setPrincipalResolver(
                new RequestAttributePrincipalResolver()
        );

        return builder
                .baseUrl("http://localhost:9091")
                .requestInterceptor(interceptor)
                .build();
    }
}