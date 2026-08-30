package com.desafio.integrados.pontogps.exception;

/**
 * Exceção de negócio lançada quando coordenadas inválidas ou parâmetros inconsistentes são fornecidos.
 */
public class InvalidCoordinateException extends RuntimeException {

    public InvalidCoordinateException(String message) {
        super(message);
    }
}
