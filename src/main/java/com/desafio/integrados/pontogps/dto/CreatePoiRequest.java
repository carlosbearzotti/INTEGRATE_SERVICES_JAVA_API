package com.desafio.integrados.pontogps.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Record DTO para requisição de criação de um novo Ponto de Interesse.
 * Suporta os campos em inglês ("name", "x", "y") ou português ("nome", "x", "y").
 */
public record CreatePoiRequest(

        @JsonProperty("name")
        @JsonAlias({"nome", "poi"})
        @NotBlank(message = "O nome do POI é obrigatório e não pode conter apenas espaços em branco.")
        @Size(max = 150, message = "O nome do POI deve ter no máximo 150 caracteres.")
        String name,

        @JsonProperty("x")
        @JsonAlias({"coordenadaX", "coordX"})
        @NotNull(message = "A coordenada X é obrigatória.")
        @Min(value = 0, message = "A coordenada X deve ser um inteiro não negativo (x >= 0).")
        Integer x,

        @JsonProperty("y")
        @JsonAlias({"coordenadaY", "coordY"})
        @NotNull(message = "A coordenada Y é obrigatória.")
        @Min(value = 0, message = "A coordenada Y deve ser um inteiro não negativo (y >= 0).")
        Integer y
) {
}
