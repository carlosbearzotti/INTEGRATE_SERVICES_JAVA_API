package com.desafio.integrados.pontogps.domain;

import java.io.Serializable;
import java.util.Objects;

/**
 * Value Object imutável representando uma coordenada 2D inteira não negativa (X, Y).
 */
public record Coordinate(int x, int y) implements Serializable {

    public Coordinate {
        if (x < 0) {
            throw new IllegalArgumentException("A coordenada X deve ser um inteiro não negativo (x >= 0). Recebido: " + x);
        }
        if (y < 0) {
            throw new IllegalArgumentException("A coordenada Y deve ser um inteiro não negativo (y >= 0). Recebido: " + y);
        }
    }

    /**
     * Calcula a distância euclidiana exata até outra coordenada.
     * Fórmula: d = sqrt((x1 - x2)^2 + (y1 - y2)^2)
     */
    public double distanceTo(Coordinate other) {
        Objects.requireNonNull(other, "A coordenada de destino não pode ser nula.");
        long deltaX = (long) this.x - other.x;
        long deltaY = (long) this.y - other.y;
        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
    }

    /**
     * Calcula o quadrado da distância euclidiana (evita operações de raiz quadrada desnecessárias).
     */
    public long distanceSquaredTo(Coordinate other) {
        Objects.requireNonNull(other, "A coordenada de destino não pode ser nula.");
        long deltaX = (long) this.x - other.x;
        long deltaY = (long) this.y - other.y;
        return (deltaX * deltaX) + (deltaY * deltaY);
    }

    /**
     * Verifica se outra coordenada está dentro do raio de distância máxima especificado.
     */
    public boolean isWithinDistance(Coordinate other, double maxDistance) {
        if (maxDistance < 0) {
            throw new IllegalArgumentException("A distância máxima deve ser não negativa (d-max >= 0). Recebido: " + maxDistance);
        }
        return distanceSquaredTo(other) <= (maxDistance * maxDistance);
    }
}
