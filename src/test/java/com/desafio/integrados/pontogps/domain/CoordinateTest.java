package com.desafio.integrados.pontogps.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.offset;

@DisplayName("Testes Unitários do Value Object Coordinate")
class CoordinateTest {

    @Test
    @DisplayName("Deve criar coordenada com valores inteiros não negativos válidos")
    void shouldCreateCoordinateWithValidNonNegativeValues() {
        Coordinate coordinate = new Coordinate(20, 10);

        assertThat(coordinate.x()).isEqualTo(20);
        assertThat(coordinate.y()).isEqualTo(10);
    }

    @Test
    @DisplayName("Deve rejeitar coordenada X negativa")
    void shouldRejectNegativeX() {
        assertThatThrownBy(() -> new Coordinate(-1, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A coordenada X deve ser um inteiro não negativo");
    }

    @Test
    @DisplayName("Deve rejeitar coordenada Y negativa")
    void shouldRejectNegativeY() {
        assertThatThrownBy(() -> new Coordinate(10, -5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A coordenada Y deve ser um inteiro não negativo");
    }

    @Test
    @DisplayName("Deve calcular a distância euclidiana exata com o ponto de referência do desafio")
    void shouldCalculateExactEuclideanDistances() {
        Coordinate ref = new Coordinate(20, 10);

        // Lanchonete (27, 12): sqrt((27-20)^2 + (12-10)^2) = sqrt(49 + 4) = sqrt(53) ~= 7.2801
        Coordinate lanchonete = new Coordinate(27, 12);
        assertThat(ref.distanceTo(lanchonete)).isCloseTo(7.2801, offset(0.001));
        assertThat(ref.isWithinDistance(lanchonete, 10)).isTrue();

        // Joalheria (15, 12): sqrt((15-20)^2 + (12-10)^2) = sqrt(25 + 4) = sqrt(29) ~= 5.3851
        Coordinate joalheria = new Coordinate(15, 12);
        assertThat(ref.distanceTo(joalheria)).isCloseTo(5.3851, offset(0.001));
        assertThat(ref.isWithinDistance(joalheria, 10)).isTrue();

        // Pub (12, 8): sqrt((12-20)^2 + (8-10)^2) = sqrt(64 + 4) = sqrt(68) ~= 8.2462
        Coordinate pub = new Coordinate(12, 8);
        assertThat(ref.distanceTo(pub)).isCloseTo(8.2462, offset(0.001));
        assertThat(ref.isWithinDistance(pub, 10)).isTrue();

        // Supermercado (23, 6): sqrt((23-20)^2 + (6-10)^2) = sqrt(9 + 16) = sqrt(25) = 5.0
        Coordinate supermercado = new Coordinate(23, 6);
        assertThat(ref.distanceTo(supermercado)).isEqualTo(5.0);
        assertThat(ref.isWithinDistance(supermercado, 10)).isTrue();

        // Posto (31, 18): sqrt((31-20)^2 + (18-10)^2) = sqrt(121 + 64) = sqrt(185) ~= 13.6014 (> 10)
        Coordinate posto = new Coordinate(31, 18);
        assertThat(ref.distanceTo(posto)).isCloseTo(13.6014, offset(0.001));
        assertThat(ref.isWithinDistance(posto, 10)).isFalse();

        // Floricultura (19, 21): sqrt((19-20)^2 + (21-10)^2) = sqrt(1 + 121) = sqrt(122) ~= 11.0453 (> 10)
        Coordinate floricultura = new Coordinate(19, 21);
        assertThat(ref.distanceTo(floricultura)).isCloseTo(11.0453, offset(0.001));
        assertThat(ref.isWithinDistance(floricultura, 10)).isFalse();

        // Churrascaria (28, 2): sqrt((28-20)^2 + (2-10)^2) = sqrt(64 + 64) = sqrt(128) ~= 11.3137 (> 10)
        Coordinate churrascaria = new Coordinate(28, 2);
        assertThat(ref.distanceTo(churrascaria)).isCloseTo(11.3137, offset(0.001));
        assertThat(ref.isWithinDistance(churrascaria, 10)).isFalse();
    }

    @Test
    @DisplayName("Distância para o próprio ponto deve ser 0")
    void distanceToSelfShouldBeZero() {
        Coordinate p = new Coordinate(5, 5);
        assertThat(p.distanceTo(p)).isEqualTo(0.0);
        assertThat(p.isWithinDistance(p, 0)).isTrue();
    }

    @Test
    @DisplayName("Deve rejeitar distância máxima negativa")
    void shouldRejectNegativeMaxDistance() {
        Coordinate p1 = new Coordinate(0, 0);
        Coordinate p2 = new Coordinate(1, 1);

        assertThatThrownBy(() -> p1.isWithinDistance(p2, -1.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A distância máxima deve ser não negativa");
    }
}
