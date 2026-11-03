package kh.edu.istad.platform.order.client.adapter;

import kh.edu.istad.platform.order.domain.dto.QueryCustomerResult;
import kh.edu.istad.platform.order.domain.port.out.client.CustomerServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.springframework.security.oauth2.client.web.client
        .RequestAttributeClientRegistrationIdResolver.clientRegistrationId;
import static org.springframework.security.oauth2.client.web.client
        .RequestAttributePrincipalResolver.principal;

@Component
@RequiredArgsConstructor
public class CustomerServiceClientAdapter implements CustomerServiceClient {

    @Qualifier("customerServiceRestClient")
    private final RestClient restClient;

    @Override
    public List<QueryCustomerResult> getCustomerNames() {

        return restClient.get()
                .uri("/api/customers")
                .attributes(clientRegistrationId("customer-service"))
                .attributes(principal("order-service"))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}