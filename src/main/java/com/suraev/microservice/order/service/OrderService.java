package com.suraev.microservice.order.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.suraev.microservice.order.domain.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrderService {

    private final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${spring.application.microservice-customer.url}")
    private String customerBaseUrl;

    private static final String CUSTOMER_ORDER_URL = "customerOrders/";

    public void createOrder(Order order) {
        final var url = customerBaseUrl+CUSTOMER_ORDER_URL+order.getCustomerId();
        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        log.info("Order request URL: {}", url);

        try {
            final var request = new HttpEntity<>(order, headers);
            final var responseEntity = restTemplate.postForEntity(url,request,Order.class);

            if(responseEntity.getStatusCode().isError()) {
                log.error("For Order ID: {}, error response: {} is received to create Order in Customer Microservice",
                        order.getId(), responseEntity.getStatusCode());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        String.format("For Order UUID: %s, Customer Microservice Message: %s", order.getId(), responseEntity.getStatusCodeValue()));
            }
            if(responseEntity.hasBody()) {
                log.error("Order From Responce: {}", responseEntity.getBody().getId());
            }
        } catch (Exception e) {
            log.error("For Order ID: {}, cannot create Order in Customer Microservice for reason: {}",
                    order.getId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("For Order ID: %s, Customer Microservice Response: %d",
                    order.getId(), e.getMessage()));
        }
    }

    public void updateOrder(Order order) {
        final var url = customerBaseUrl+CUSTOMER_ORDER_URL+order.getCustomerId();
        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        log.info("Order Request URL: {}", url);

        try{
           final var request = new HttpEntity<>(order, headers);
           restTemplate.put(url, request);
        }  catch (Exception e) {
            log.error("For Order ID: {}, cannot create Order in Customer Microservice for reason: {}",order.getId(),e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("For Order ID: %s, Customer Microservice Response: %d", order.getId(),e.getMessage()));
        }
    }

    public void deleteOrder(Order order) {
        final var url = customerBaseUrl+CUSTOMER_ORDER_URL+order.getCustomerId()+"/"+order.getId();

        log.info("Order Request URL {}", url);

        try{
            restTemplate.delete(url);
        } catch (Exception e) {
                log.error("For Order ID: {}, cannot create Order in Customer Microservice for reason: {}", order.getId(),e.getMessage());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("For Order ID: %s, Customer Microservice Response: %d", order.getId(),e.getMessage()));
        }
    }

}
