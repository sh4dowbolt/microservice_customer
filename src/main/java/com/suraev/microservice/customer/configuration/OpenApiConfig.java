package com.suraev.microservice.customer.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(info = @Info(
        title = "microservice_customer",
        description = "Customer Microservice", version = "0.0.1-SNAPSHOT",
        contact = @Contact(
                name = "Suraev Vitalij",
                email = "suraevvvitaly@gmail.com",
                url = "https://t.me/sh4dowb0lt"
        )
))
public class OpenApiConfig {
}
