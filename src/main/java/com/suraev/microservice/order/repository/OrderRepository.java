package com.suraev.microservice.order.repository;

import com.suraev.microservice.order.domain.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {
}
