package com.desafio.integrados.pontogps.service;

import com.desafio.integrados.pontogps.domain.PointOfInterest;
import com.desafio.integrados.pontogps.dto.CreatePoiRequest;
import com.desafio.integrados.pontogps.dto.NearbyPoiResponse;
import com.desafio.integrados.pontogps.dto.PoiResponse;
import com.desafio.integrados.pontogps.exception.InvalidCoordinateException;
import com.desafio.integrados.pontogps.repository.PointOfInterestRepository;
import com.desafio.integrados.pontogps.service.impl.PointOfInterestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários do Serviço PointOfInterestService")
class PointOfInterestServiceTest {

    @Mock
    private PointOfInterestRepository repository;

    @InjectMocks
    private PointOfInterestServiceImpl service;

    private List<PointOfInterest> samplePois;

    @BeforeEach
    void setUp() {
        samplePois = List.of(
                new PointOfInterest(1L, "Lanchonete", 27, 12),
                new PointOfInterest(2L, "Posto", 31, 18),
                new PointOfInterest(3L, "Joalheria", 15, 12),
                new PointOfInterest(4L, "Floricultura", 19, 21),
                new PointOfInterest(5L, "Pub", 12, 8),
                new PointOfInterest(6L, "Supermercado", 23, 6),
                new PointOfInterest(7L, "Churrascaria", 28, 2)
        );
    }

    @Test
    @DisplayName("Deve cadastrar um POI com sucesso")
    void shouldCreatePoiSuccessfully() {
        CreatePoiRequest request = new CreatePoiRequest("Lanchonete", 27, 12);
        PointOfInterest savedPoi = new PointOfInterest(1L, "Lanchonete", 27, 12);

        when(repository.save(any(PointOfInterest.class))).thenReturn(savedPoi);

        PoiResponse response = service.create(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Lanchonete");
        assertThat(response.x()).isEqualTo(27);
        assertThat(response.y()).isEqualTo(12);

        verify(repository).save(any(PointOfInterest.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar POI com coordenadas negativas")
    void shouldThrowExceptionWhenCreatingWithNegativeCoordinates() {
        CreatePoiRequest requestWithNegativeX = new CreatePoiRequest("Lanchonete", -1, 12);
        CreatePoiRequest requestWithNegativeY = new CreatePoiRequest("Lanchonete", 27, -5);

        assertThatThrownBy(() -> service.create(requestWithNegativeX))
                .isInstanceOf(InvalidCoordinateException.class)
                .hasMessageContaining("As coordenadas devem ser inteiros não negativos");

        assertThatThrownBy(() -> service.create(requestWithNegativeY))
                .isInstanceOf(InvalidCoordinateException.class)
                .hasMessageContaining("As coordenadas devem ser inteiros não negativos");
    }

    @Test
    @DisplayName("Deve listar todos os POIs cadastrados")
    void shouldFindAllPois() {
        when(repository.findAll()).thenReturn(samplePois);

        List<PoiResponse> result = service.findAll();

        assertThat(result).hasSize(7);
        assertThat(result.get(0).name()).isEqualTo("Lanchonete");
        assertThat(result.get(1).name()).isEqualTo("Posto");
    }

    @Test
    @DisplayName("Deve retornar os 4 POIs próximos para referência (x=20, y=10, dmax=10)")
    void shouldFindNearbyPoisMatchingChallengeExample() {
        List<PointOfInterest> expectedNearbyFromRepo = List.of(
                new PointOfInterest(1L, "Lanchonete", 27, 12),
                new PointOfInterest(3L, "Joalheria", 15, 12),
                new PointOfInterest(5L, "Pub", 12, 8),
                new PointOfInterest(6L, "Supermercado", 23, 6)
        );

        when(repository.findNearby(20, 10, 10.0)).thenReturn(expectedNearbyFromRepo);

        List<NearbyPoiResponse> result = service.findNearby(20, 10, 10.0);

        assertThat(result).hasSize(4);
        assertThat(result).extracting(NearbyPoiResponse::name)
                .containsExactlyInAnyOrder("Lanchonete", "Joalheria", "Pub", "Supermercado");

        // Supermercado tem distância 5.0m (a menor entre os 4), seguido de Joalheria (5.39m)
        assertThat(result.get(0).name()).isEqualTo("Supermercado");
        assertThat(result.get(0).distanceInMeters()).isEqualTo(5.0);
    }

    @Test
    @DisplayName("Deve rejeitar consulta com coordenadas de referência negativas")
    void shouldRejectNegativeReferenceCoordinates() {
        assertThatThrownBy(() -> service.findNearby(-1, 10, 10.0))
                .isInstanceOf(InvalidCoordinateException.class)
                .hasMessageContaining("As coordenadas de referência devem ser inteiros não negativos");

        assertThatThrownBy(() -> service.findNearby(10, -5, 10.0))
                .isInstanceOf(InvalidCoordinateException.class)
                .hasMessageContaining("As coordenadas de referência devem ser inteiros não negativos");
    }

    @Test
    @DisplayName("Deve rejeitar consulta com d-max negativo")
    void shouldRejectNegativeMaxDistance() {
        assertThatThrownBy(() -> service.findNearby(20, 10, -10.0))
                .isInstanceOf(InvalidCoordinateException.class)
                .hasMessageContaining("A distância máxima (d-max) deve ser um número não negativo");
    }
}
