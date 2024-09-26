package com.suraev.microservice.customer.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Schema(description = "Ответ с состоянием работоспособности микросервиса")
public class Health {
    @Schema(description = "Статус микросервиса", example = "UP")
    private HealthStatus status;
}
