package com.suraev.microservice.customer.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "customer")
@Data
@NoArgsConstructor
@Schema(description = "Сущность клиента")
public class Customer implements Serializable {

    private static final long serialVersionUID=1L;

    @Id
    @Schema(description = "Идентификатор клиента", accessMode = Schema.AccessMode.READ_ONLY)
    private String id;

    @Field("orders")
    @Schema(description = "Список заказов клиента")
    private Set<Order> orders = new HashSet<>();

    public Customer addOrder(Order order) {
        this.orders.add(order);
        return this;
    }


}
