package com.desafio.integrados.encurtadorurl.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "app.shortener")
@Validated
public class UrlShortenerProperties {

    @Min(value = 5, message = "O tamanho do código encurtado deve ter no mínimo 5 caracteres")
    @Max(value = 10, message = "O tamanho do código encurtado deve ter no máximo 10 caracteres")
    private int codeLength = 6;

    @Min(value = 1, message = "O prazo de expiração deve ser de no mínimo 1 dia")
    private long expirationDays = 30;

    private String baseUrl;

    public int getCodeLength() {
        return codeLength;
    }

    public void setCodeLength(int codeLength) {
        this.codeLength = codeLength;
    }

    public long getExpirationDays() {
        return expirationDays;
    }

    public void setExpirationDays(long expirationDays) {
        this.expirationDays = expirationDays;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
