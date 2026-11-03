package kh.edu.istad.platform.order.domain.port.out.client;

import kh.edu.istad.platform.order.domain.dto.QueryCustomerResult;
import reactor.core.publisher.Flux;

import java.util.List;

public interface CustomerServiceClient {

    List<QueryCustomerResult> getCustomerNames();

}
