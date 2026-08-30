package com.desafio.integrados.pontogps.service.impl;

import com.desafio.integrados.pontogps.domain.Coordinate;
import com.desafio.integrados.pontogps.domain.PointOfInterest;
import com.desafio.integrados.pontogps.dto.CreatePoiRequest;
import com.desafio.integrados.pontogps.dto.NearbyPoiResponse;
import com.desafio.integrados.pontogps.dto.PoiResponse;
import com.desafio.integrados.pontogps.exception.InvalidCoordinateException;
import com.desafio.integrados.pontogps.repository.PointOfInterestRepository;
import com.desafio.integrados.pontogps.service.PointOfInterestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

/**
 * Implementação do serviço de gerenciamento e consulta de Pontos de Interesse.
 */
@Service
public class PointOfInterestServiceImpl implements PointOfInterestService {

    private static final Logger log = LoggerFactory.getLogger(PointOfInterestServiceImpl.class);

    private final PointOfInterestRepository repository;

    public PointOfInterestServiceImpl(PointOfInterestRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public PoiResponse create(CreatePoiRequest request) {
        log.info("Cadastrando novo POI: '{}' nas coordenadas (x={}, y={})", request.name(), request.x(), request.y());

        if (request.x() < 0 || request.y() < 0) {
            throw new InvalidCoordinateException("As coordenadas devem ser inteiros não negativos (x >= 0, y >= 0).");
        }

        PointOfInterest poi = new PointOfInterest(request.name().trim(), request.x(), request.y());
        PointOfInterest saved = repository.save(poi);

        log.info("POI cadastrado com sucesso. ID: {}", saved.getId());
        return PoiResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PoiResponse> findAll() {
        log.info("Listando todos os POIs cadastrados");
        return repository.findAll()
                .stream()
                .map(PoiResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NearbyPoiResponse> findNearby(int x, int y, double maxDistance) {
        log.info("Consultando POIs próximos à referência (x={}, y={}) com d-max={}m", x, y, maxDistance);

        if (x < 0 || y < 0) {
            throw new InvalidCoordinateException(
                    String.format("As coordenadas de referência devem ser inteiros não negativos. Recebido: (x=%d, y=%d)", x, y)
            );
        }

        if (maxDistance < 0) {
            throw new InvalidCoordinateException(
                    String.format("A distância máxima (d-max) deve ser um número não negativo. Recebido: %.2f", maxDistance)
            );
        }

        Coordinate reference = new Coordinate(x, y);

        // Busca os pontos que satisfazem a distância euclidiana diretamente no banco
        List<PointOfInterest> nearbyList = repository.findNearby(x, y, maxDistance);

        // Mapeia para DTO com distância calculada e ordena pela menor distância para melhor usabilidade
        List<NearbyPoiResponse> responses = nearbyList.stream()
                .map(poi -> NearbyPoiResponse.fromEntityAndReference(poi, reference))
                .sorted(Comparator.comparingDouble(NearbyPoiResponse::distanceInMeters))
                .toList();

        log.info("Consulta por proximidade concluída: {} POI(s) encontrado(s)", responses.size());
        return responses;
    }
}
