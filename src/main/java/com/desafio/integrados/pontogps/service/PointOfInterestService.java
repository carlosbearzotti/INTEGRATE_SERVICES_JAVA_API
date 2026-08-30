package com.desafio.integrados.pontogps.service;

import com.desafio.integrados.pontogps.dto.CreatePoiRequest;
import com.desafio.integrados.pontogps.dto.NearbyPoiResponse;
import com.desafio.integrados.pontogps.dto.PoiResponse;

import java.util.List;

/**
 * Interface de regras de negócio para Pontos de Interesse (POIs).
 */
public interface PointOfInterestService {

    /**
     * Cadastra um novo Ponto de Interesse.
     */
    PoiResponse create(CreatePoiRequest request);

    /**
     * Lista todos os Pontos de Interesse cadastrados.
     */
    List<PoiResponse> findAll();

    /**
     * Lista os Pontos de Interesse dentro do raio de distância máxima a partir do ponto de referência.
     *
     * @param x Coordenada X de referência (>= 0)
     * @param y Coordenada Y de referência (>= 0)
     * @param maxDistance Distância máxima permitida em metros (>= 0)
     * @return Lista de POIs próximos com a distância calculada
     */
    List<NearbyPoiResponse> findNearby(int x, int y, double maxDistance);
}
