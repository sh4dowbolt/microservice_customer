package com.suraev.microservice.customer.rest;


import com.suraev.microservice.customer.domain.Customer;
import com.suraev.microservice.customer.repository.CustomerRepository;
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
public class CustomerResource {
    private final Logger log = LoggerFactory.getLogger(CustomerResource.class);

    private static final String ENTITY_NAME = "customer";

    @Value("${spring.application.name}")
    private String applicationName;

    private final CustomerRepository customerRepository;

    public CustomerResource(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }


    /**
     * {@Code POST /customers} : создать новый заказ
     * @param customer - это клиент необходимый для создания
     * @return {@link ResponseEntity} со статусом {@Code 201 (Created)} с телом нового заказа, или со статусом {@Code 409 (Conflict)} если у клиента уже есть ID.
     * @throws ResponseStatusException если синтаксис ссылки нарушен
     */
    @PostMapping("/customers")
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer) throws URISyntaxException {
        log.debug("REST request to save Customer: {},",customer);
        if(customer.getId()!= null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"A new customer cannot already have an ID");
        }
        var result = customerRepository.save(customer);

        HttpHeaders headers = new HttpHeaders();
        String message = String.format("A new %s is created with identifier %s",ENTITY_NAME,customer.getId());
        headers.add("X-" + applicationName + "-alert", message);
        headers.add("X-" + applicationName + "-params", customer.getId());

        return ResponseEntity.created(new URI("/api/customers/"+result.getId())).headers(headers).body(result);
    }


    /**
     * {@Code PUT /customers} : обновление существующего клиента
     * @param customer - клиент необходимый для обновления
     * @return {@link ResponseEntity} со статусом {@Code 200 (OK)} и телом обновленного клиента,
     * или со статусом {@Code 409 (Conflict)} если клиент некорректный,
     * или со статусом {@Code 500 (Internal Server Error)} если клиент не может быть обновлен.
     * @throws ResponseStatusException если синтаксис ссылки некорректен.
     */
    @PutMapping("/customers")
    public ResponseEntity<Customer> updateCustomer(@Valid @RequestBody Customer customer) {
        log.debug("REST request to update Customer: {}", customer);

        if(customer.getId()==null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An existing customer should have an id");
        }
        var result = customerRepository.save(customer);

        HttpHeaders headers = new HttpHeaders();
        String message = String.format("A %s is updated with identifier %s",ENTITY_NAME,customer.getId());
        headers.add("X-" + applicationName + "-alert", message);
        headers.add("X-" + applicationName + "-params", customer.getId());

        return ResponseEntity.ok().headers(headers).body(result);

    }

    /**
     * {@Code GET /customers/:id} : получить клиента по ID
     * @param id - ID клиента
     * @return {@link ResponseEntity} со статусом {@Code 200 (OK)}  с телом заказа, или со статусом {@Code 404 (Not found)}.
     * @throws Exception если не существует клиента с таким ID.
     */

    @GetMapping("/customers/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable String id) {
        log.debug("REST request to get Customer: {}", id);
        try {
            final var result = customerRepository.findById(id).orElseThrow();
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * {@Code GET /customers} : получить список всех клиентов
     * @return список всех клиентов со статусом {@Code 200 (OK)}
     */
    @GetMapping("/customers")
    public List<Customer> getAllCustomers() {
        log.debug("REST request to get all Customers");
        return customerRepository.findAll();
    }

    /**
     * {@Code DELETE /customers/:id} : удалить клиента по ID
     * @param id ID клиеннта
     * @return {@link ResponseEntity} со статусом {@Code 204 (NO_CONTENT)}
     */
    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String id) {
        log.debug("REST request to delete Customer: {}", id);
        customerRepository.deleteById(id);

        HttpHeaders headers= new HttpHeaders();
        String message = String.format("A %s is deleted with identifier %s", ENTITY_NAME, id);
        headers.add(applicationName, message);

        return ResponseEntity.noContent().headers(headers).build();
    }




    }






