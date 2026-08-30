package com.desafio.integrados.encurtadorurl.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record ShortenUrlRequest(
        @NotBlank(message = "A URL não pode estar vazia")
        @URL(message = "A URL informada possui formato inválido")
        String url
) {}
