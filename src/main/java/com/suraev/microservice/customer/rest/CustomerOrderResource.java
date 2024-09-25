package com.suraev.microservice.customer.rest;


import com.suraev.microservice.customer.domain.Customer;
import com.suraev.microservice.customer.domain.Order;
import com.suraev.microservice.customer.exceptions.BadRequestAlertException;
import com.suraev.microservice.customer.repository.CustomerRepository;
import com.suraev.microservice.customer.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@Tag(name="Взаимодействие с микросервисом заказов", description = "Обработка запросов с микросервиса заказов")
public class CustomerOrderResource {
    private final Logger log = LoggerFactory.getLogger(CustomerOrderResource.class);
    private static final String ENTITY_NAME = "customer";
    @Value("${spring.application.name}")
    private String applicationName;
    private final CustomerRepository customerRepository;

    public CustomerOrderResource(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @PostMapping("/customerOrders/{customerId}")
    @Operation(summary = "Создать заказ для определенного клиента", description = "Позволяет создавать заказ для определенного клиента")
    public ResponseEntity<com.suraev.microservice.customer.domain.Order> createOrder(@PathVariable String customerId,
                                                                                     @Valid @RequestBody Order order) {

        log.debug("REST request to save Order: {} for Customer ID: {}", order, customerId);

        if (customerId.isBlank()) {
            throw new BadRequestAlertException("No customer", ENTITY_NAME,"noid");
        }

        final Optional<Customer> customerOptional =
                customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            final var customer = customerOptional.get();
            customer.addOrder(order);
            customerRepository.save(customer);
            return ResponseEntity.ok().body(order);

        } else {
            throw new BadRequestAlertException("Invalid Customer", ENTITY_NAME, "invalidcustomer");
        }
    }

    @PutMapping("/customerOrders/{customerId}")
    @Operation(summary = "Обновление существующего заказа у определенного клиента", description = "Позволяет обновить существующий заказ у определенного клиента")
    @Transactional
    public ResponseEntity<Order> updateOrder(@PathVariable String customerId, @Valid @RequestBody Order order) {

        if (customerId.isBlank()) {
            throw new BadRequestAlertException("No customer",ENTITY_NAME,"noid");
        }

        final var customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            final var customer = customerOptional.get();
            final var orderSet = customer.getOrders().stream().map(x -> Objects.equals(x.getId(), order.getId()) ? order : x).collect(Collectors.toSet());

            customer.setOrders(orderSet);

            customerRepository.save(customer);

            return ResponseEntity.ok().body(order);

        } else {
            throw new BadRequestAlertException("Invalid Customer", ENTITY_NAME,"invalidcustomer");
        }
    }

    @Operation(summary = "Получить все заказы определенного клиента", description = "Позволяет получить все заказы определенного клиента")
    @GetMapping("/customerOrders/{customerId}")
    public Set<Order> getAllOrders(@PathVariable String customerId) {
        log.debug("REST request to get all Order for Customer: {}", customerId);
        if (customerId.isBlank()) {
            throw new BadRequestAlertException("No Customer", ENTITY_NAME, "noid");
        }
        final var customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            final var customer = customerOptional.get();
            return customer.getOrders();
        } else {
            throw new BadRequestAlertException("Invalid Customer", ENTITY_NAME, "invalidcustomer");
        }
    }

    @Operation(summary = "Получить определенный заказ у конкретного клиента", description = "Позволяет получить определенный заказ у конкретного клиента")
    @GetMapping("/customerOrders/{customerId}/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable String customerId, @PathVariable String orderId) {

        log.debug("REST request to get Order: {} for Customer: {}", orderId, customerId);

        if (customerId.isBlank()) {
            throw new BadRequestAlertException("No customer", ENTITY_NAME, "noid");

        }
        final var optionalCustomer = customerRepository.findById(customerId);
        if (optionalCustomer.isPresent()) {
            final var customer = optionalCustomer.get();
            final var optionalOrder = customer.getOrders().stream().filter(order -> Objects.equals(order.getId(), orderId)).findFirst();
            return ResponseUtil.wrapOrNotFound(optionalOrder);
            } else {
                throw new BadRequestAlertException("Invalid Customer", ENTITY_NAME, "invalidcustomer");
            }
        }

    @Operation(summary = "Удалить определенный заказ у указанного клиента", description = "Позволяет удалить определенный заказ у указанного клиента")
    @DeleteMapping("/customerOrders/{customerId}/{orderId}")
    @Transactional
        public ResponseEntity<Void> deleteOrder(@PathVariable String customerId, @PathVariable String orderId) {
        log.debug("REST request to delete Order: {} for Customer: {}", orderId, customerId);

        if(customerId.isBlank()) {
            throw new BadRequestAlertException("No customer", ENTITY_NAME, "noid");
        }

        final var optionalCustomer = customerRepository.findById(customerId);
        if(optionalCustomer.isPresent()) {
            final var customer = optionalCustomer.get();
            customer.getOrders().removeIf(x->Objects.equals(x.getId(),orderId));
            customerRepository.save(customer);
            return ResponseEntity.noContent().build();
        }
        throw new BadRequestAlertException("Invalid Customer", ENTITY_NAME,"invalid customer");
    }




}
