package com.desafio.integrados.pontogps.repository;

import com.desafio.integrados.pontogps.domain.PointOfInterest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Testes de Integração do Repositório PointOfInterestRepository")
class PointOfInterestRepositoryTest {

    @Autowired
    private PointOfInterestRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        List<PointOfInterest> pois = List.of(
                new PointOfInterest("Lanchonete", 27, 12),
                new PointOfInterest("Posto", 31, 18),
                new PointOfInterest("Joalheria", 15, 12),
                new PointOfInterest("Floricultura", 19, 21),
                new PointOfInterest("Pub", 12, 8),
                new PointOfInterest("Supermercado", 23, 6),
                new PointOfInterest("Churrascaria", 28, 2)
        );

        repository.saveAll(pois);
    }

    @Test
    @DisplayName("Deve filtrar POIs por proximidade no banco com os 4 pontos esperados do desafio")
    void shouldFindNearbyPoisFromDatabase() {
        List<PointOfInterest> nearby = repository.findNearby(20, 10, 10.0);

        assertThat(nearby).hasSize(4);
        assertThat(nearby).extracting(PointOfInterest::getName)
                .containsExactlyInAnyOrder("Lanchonete", "Joalheria", "Pub", "Supermercado");
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando nenhum ponto estiver dentro de d-max")
    void shouldReturnEmptyWhenNoPoisAreWithinDistance() {
        List<PointOfInterest> nearby = repository.findNearby(100, 100, 10.0);

        assertThat(nearby).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar ponto exato quando d-max for 0")
    void shouldReturnExactPointWhenDistanceIsZero() {
        List<PointOfInterest> nearby = repository.findNearby(23, 6, 0.0);

        assertThat(nearby).hasSize(1);
        assertThat(nearby.get(0).getName()).isEqualTo("Supermercado");
    }
}
