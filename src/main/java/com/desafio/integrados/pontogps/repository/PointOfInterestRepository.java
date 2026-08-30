package com.desafio.integrados.pontogps.repository;

import com.desafio.integrados.pontogps.domain.PointOfInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório Spring Data JPA para operações de persistência e consulta de Pontos de Interesse.
 */
@Repository
public interface PointOfInterestRepository extends JpaRepository<PointOfInterest, Long> {

    /**
     * Consulta otimizada por proximidade calculando o quadrado da distância euclidiana diretamente no banco:
     * ((p.x - x)^2 + (p.y - y)^2) <= (maxDist * maxDist)
     */
    @Query("""
        SELECT p FROM PointOfInterest p
        WHERE (((p.x - :x) * (p.x - :x)) + ((p.y - :y) * (p.y - :y))) <= (:maxDistance * :maxDistance)
    """)
    List<PointOfInterest> findNearby(
            @Param("x") int x,
            @Param("y") int y,
            @Param("maxDistance") double maxDistance
    );
}
