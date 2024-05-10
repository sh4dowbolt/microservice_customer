package com.suraev.microservice.customer.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Document(collation = "customer")
@Data
@NoArgsConstructor
public class Customer implements Serializable {

    private static final long serialVersionUID=1L;

    @Id
    private String id;

    @Field("orders")
    private Set<Order> orders = new HashSet<>();

    public Customer addOrder(Order order) {
        this.orders.add(order);
        return this;
    }


}
