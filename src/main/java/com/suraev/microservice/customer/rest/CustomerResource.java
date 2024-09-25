package com.suraev.microservice.customer.rest;


import com.suraev.microservice.customer.domain.Customer;
import com.suraev.microservice.customer.exceptions.BadRequestAlertException;
import com.suraev.microservice.customer.repository.CustomerRepository;
import com.suraev.microservice.customer.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@Tag(name="Управление клиентом", description = "Панель управления клиентами")
public class CustomerResource {
    private final Logger log = LoggerFactory.getLogger(CustomerResource.class);

    private static final String ENTITY_NAME = "customer";

    @Value("${spring.application.name}")
    private String applicationName;

    private final CustomerRepository customerRepository;

    public CustomerResource(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @PostMapping("/customers")
    @Operation(summary = "Создание клиента", description = "Позволяет создавать клиента")
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer) throws URISyntaxException {
        log.debug("REST request to save Customer: {},", customer);
        if (customer.getId() != null) {
            throw new BadRequestAlertException("A new customer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = customerRepository.save(customer);

        HttpHeaders headers = new HttpHeaders();
        String message = String.format("A new %s is created with identifier %s", ENTITY_NAME, customer.getId());
        headers.add("X-" + applicationName + "-alert", message);
        headers.add("X-" + applicationName + "-params", customer.getId());

        return ResponseEntity.created(new URI("/api/customers/" + result.getId())).headers(headers).body(result);
    }

    @PutMapping("/customers")
    @Operation(summary = "Обновление клиента", description = "Позволяет обновлять клиента")
    public ResponseEntity<Customer> updateCustomer(@Valid @RequestBody Customer customer) {
        log.debug("REST request to update Customer: {}", customer);

        if (customer.getId() == null) {
            throw new BadRequestAlertException("An existing customer should have an id", ENTITY_NAME, "iddontexist");
        }
        var result = customerRepository.save(customer);

        HttpHeaders headers = new HttpHeaders();
        String message = String.format("A %s is updated with identifier %s", ENTITY_NAME, customer.getId());
        headers.add("X-" + applicationName + "-alert", message);
        headers.add("X-" + applicationName + "-params", customer.getId());

        return ResponseEntity.ok().headers(headers).body(result);
    }

    @Operation(summary = "Получить клиента по ID", description = "Позволяет получить клиента по ID")
    @GetMapping("/customers/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable String id) {
        log.debug("REST request to get Customer: {}", id);
        final var customer = customerRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(customer);
    }

    @Operation(summary = "Получить список клиентов", description = "Позволяет получить список клиентов")
    @GetMapping("/customers")
    public List<Customer> getAllCustomers() {
        log.debug("REST request to get all Customers");
        return customerRepository.findAll();
    }

    @DeleteMapping("/customers/{id}")
    @Operation(summary = "Удаление клиента", description = "Позволяет удалить клиента")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String id) {
        log.debug("REST request to delete Customer: {}", id);
        customerRepository.deleteById(id);

        HttpHeaders headers = new HttpHeaders();
        String message = String.format("A %s is deleted with identifier %s", ENTITY_NAME, id);
        headers.add(applicationName, message);

        return ResponseEntity.noContent().headers(headers).build();
    }
}
