package com.desafio.integrados.pontogps.dto;

import com.desafio.integrados.pontogps.domain.Coordinate;
import com.desafio.integrados.pontogps.domain.PointOfInterest;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Record DTO de resposta para consulta por proximidade contendo a distância calculada.
 */
public record NearbyPoiResponse(
        Long id,
        String name,
        Integer x,
        Integer y,
        Double distanceInMeters
) {
    public static NearbyPoiResponse fromEntityAndReference(PointOfInterest poi, Coordinate reference) {
        double dist = poi.distanceTo(reference);
        double roundedDist = BigDecimal.valueOf(dist)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        return new NearbyPoiResponse(
                poi.getId(),
                poi.getName(),
                poi.getX(),
                poi.getY(),
                roundedDist
        );
    }
}
