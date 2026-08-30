package com.desafio.integrados.pontogps.dto;

import com.desafio.integrados.pontogps.domain.PointOfInterest;

/**
 * Record DTO de resposta contendo os dados de um Ponto de Interesse.
 */
public record PoiResponse(
        Long id,
        String name,
        Integer x,
        Integer y
) {
    public static PoiResponse fromEntity(PointOfInterest poi) {
        return new PoiResponse(
                poi.getId(),
                poi.getName(),
                poi.getX(),
                poi.getY()
        );
    }
}
