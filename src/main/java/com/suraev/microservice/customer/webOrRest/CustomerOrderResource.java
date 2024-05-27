package com.suraev.microservice.customer.webOrRest;


import com.suraev.microservice.customer.domain.Customer;
import com.suraev.microservice.customer.domain.Order;
import com.suraev.microservice.customer.repository.CustomerRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class CustomerOrderResource {
    private final Logger log = LoggerFactory.getLogger(CustomerOrderResource.class);

    private static final String ENTITY_NAME = "order";

    @Value("${spring.application.name}")
    private String applicationName;

    private final CustomerRepository customerRepository;


    public CustomerOrderResource(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }


    @PostMapping("/customerOrders/{customerId}")
    public ResponseEntity<com.suraev.microservice.customer.domain.Order> createOrder(@PathVariable String customerId,
                                                                                     @Valid @RequestBody Order order) {

        log.debug("REST request to save Order: {} for Customer ID: {}",order, customerId);

        if(customerId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No Customer: "+ENTITY_NAME);
        }

        final Optional<Customer> customerOptional =
                customerRepository.findById(customerId);

        if(customerOptional.isPresent()) {
            final var customer = customerOptional.get();
            customer.addOrder(order);
            customerRepository.save(customer);
            return ResponseEntity.ok().body(order);

        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Customer: "+ENTITY_NAME);
        }
    }


}
