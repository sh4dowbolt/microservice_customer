package com.suraev.microservice.customer.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Objects;


@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Сущность заказа")
public class Order implements Serializable {

    private static final long serialVersionUID=1L;

    @Id
    @NotBlank
    @Schema(description = "Идентификатор заказа", accessMode = Schema.AccessMode.READ_ONLY)
    private String id;

    @NotBlank
    @Schema(description = "Идентификатор клиента")
    private String customerId;
    @Schema(description = "Способ оплаты")
    private String paymentDetails;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id.equals(order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
