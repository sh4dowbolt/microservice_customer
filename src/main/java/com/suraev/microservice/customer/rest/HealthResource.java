package com.suraev.microservice.customer.rest;

import com.suraev.microservice.customer.domain.Health;
import com.suraev.microservice.customer.domain.HealthStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Tag(name ="Check health endpoint", description = "Проверка работоспособности микросервиса")
public class HealthResource {
    private final Logger log = LoggerFactory.getLogger(HealthResource.class);

    @Operation(summary = "Проверка статуса микросервиса")
    @GetMapping(value = "/health", produces = "application/json")
    public ResponseEntity<Health> getHealth() {
        log.debug("REST request to get the Health Status");
        final var health = new Health();
        health.setStatus(HealthStatus.UP);
        return ResponseEntity.ok().body(health);

    }
}
