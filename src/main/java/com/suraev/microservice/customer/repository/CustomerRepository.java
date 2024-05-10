package com.suraev.microservice.customer.repository;

import com.suraev.microservice.customer.domain.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerRepository extends MongoRepository<Customer, String> {
}
