package com.suraev.microservice.customer.rest;


import com.suraev.microservice.customer.domain.Customer;
import com.suraev.microservice.customer.domain.Order;
import com.suraev.microservice.customer.exceptions.BadRequestAlertException;
import com.suraev.microservice.customer.repository.CustomerRepository;
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
public class CustomerOrderResource {
    private final Logger log = LoggerFactory.getLogger(CustomerOrderResource.class);
    private static final String ENTITY_NAME = "customer";

    @Value("${spring.application.name}")
    private String applicationName;

    private final CustomerRepository customerRepository;


    public CustomerOrderResource(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * {@Code POST /customerOrders/{customerId}} : Создаем новый заказ для указанного клиента
     *
     * @param customerId ID покупателя.
     * @param order      заказ для создания
     * @return {@link ResponseEntity} со статусом {@Code 200 (OK)} с телом заказа или со статусом {@Code 400 (Bad Request)} если у заказа уже есть ID.
     * @throws BadRequestAlertException если синтаксис ссылки нарушен
     */
    @PostMapping("/customerOrders/{customerId}")
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


    /**
     * {@Code PUT /customOrders/:customerId} : Обновление существубщего заказа по заданному ID клиента"
     *
     * @param customerId ID клиента
     * @param order      заказ для обновления
     * @return {@link ResponseEntity} со стасусом {@Code 200 (OK)} и с телом обновленного заказа,
     * или со статусом {@Code 404 (Not Found)} если ID заказа некорректный,
     * или со статусом {@Code 500 (Internal Server Error)} если заказ не может быть обновлен.
     */
    @PutMapping("/customOrders/{customerId}")
    @Transactional
    public ResponseEntity<Order> updateOrder(@PathVariable String customerId, @Valid @RequestBody Order order) {

        if (customerId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Customer " + ENTITY_NAME);
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


    /**
     * {@Code GET /customOrder/:customerId} : получить все заказы клиента
     *
     * @param customerId - ID клиента
     * @return список заказов клиента со статусом {@Code 200 (OK)}
     * или со статусом {@Code 404 (Not Found)} если ID заказа некорректный,
     * или со статусом {@Code 500 (Internal Server Error)} если заказ не может быть обновлен.
     */
    @GetMapping("/customOrders/{customerId}")
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


    /**
     * {@Code GET /customOrder/:customerId/:orderId} : получить заказ по ID конкретного клиента
     * @param customerId - ID клиента
     * @param orderId - ID заказа
     * @return {@link ResponseEntity} со статусом {@Code 200 (OK)} и телом заказа, или статус {@Code 404 (NOT FOUND)}
     * @throws ResponseStatusException 1) если ID клиента пустой {@Code 404 (NOT FOUND)}, 2) если заказ с указанным ID не существует {@Code 404 (NOT FOUND)},
     * 3) если клиента с указанным ID не существует {@Code 500 (Internal Server Error).
     */
    @GetMapping("/customOrders/{customerId}/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable String customerId, @PathVariable String orderId) {
        log.debug("REST request to get Order: {} for Customer: {}", orderId, customerId);

        if (customerId.isBlank()) {
            throw new BadRequestAlertException("No customer", ENTITY_NAME,"noid");
        }
        final var optionalCustomer = customerRepository.findById(customerId);
        if (optionalCustomer.isPresent()) {
            final var customer = optionalCustomer.get();
            final var optionalOrder = customer.getOrders().stream().filter(order -> Objects.equals(order.getId(), orderId)).findFirst();

            if (optionalOrder.isPresent()) {
                return ResponseEntity.ok().body(optionalOrder.get());
            } else {
                throw new BadRequestAlertException("Invalid Order", ENTITY_NAME, "invalidorder");
            }
        }
        throw new BadRequestAlertException("Invalid Customer", ENTITY_NAME, "invalidcustomer");
    }

    /**
     * {@Code DELETE /customOrders/:customerId/:orderId}} : удалить заказ по ID у определенного клиента
     * @param customerId - ID клиента
     * @param orderId - ID заказа
     * @return {@link ResponseEntity} со статусом {@Code 204 (NO_CONTENT)}
     */
    @DeleteMapping("/customOrders/{customerId}/{orderId}")
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
