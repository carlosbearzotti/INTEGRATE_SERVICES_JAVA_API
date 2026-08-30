package com.desafio.integrados.pontogps.controller;

import com.desafio.integrados.pontogps.dto.CreatePoiRequest;
import com.desafio.integrados.pontogps.dto.NearbyPoiResponse;
import com.desafio.integrados.pontogps.dto.PoiResponse;
import com.desafio.integrados.pontogps.service.PointOfInterestService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controller REST para cadastro e consulta de Pontos de Interesse (POIs).
 */
@RestController
@RequestMapping({"/pois", "/api/pois"})
public class PointOfInterestController {

    private final PointOfInterestService service;

    public PointOfInterestController(PointOfInterestService service) {
        this.service = service;
    }

    /**
     * Endpoint para cadastrar um novo Ponto de Interesse.
     * [POST] /pois
     */
    @PostMapping
    public ResponseEntity<PoiResponse> create(@Valid @RequestBody CreatePoiRequest request) {
        PoiResponse response = service.create(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    /**
     * Endpoint para listar todos os Pontos de Interesse cadastrados.
     * [GET] /pois
     */
    @GetMapping
    public ResponseEntity<List<PoiResponse>> findAll() {
        List<PoiResponse> pois = service.findAll();
        return ResponseEntity.ok(pois);
    }

    /**
     * Endpoint para listar Pontos de Interesse por proximidade.
     * [GET] /pois/nearby?x={x}&y={y}&dmax={dmax}
     * Também responde em /pois/proximidade para conveniência em língua portuguesa.
     */
    @GetMapping({"/nearby", "/proximidade"})
    public ResponseEntity<List<NearbyPoiResponse>> findNearby(
            @RequestParam("x") int x,
            @RequestParam("y") int y,
            @RequestParam(name = "dmax", required = false, defaultValue = "10") double dmax
    ) {
        List<NearbyPoiResponse> nearbyPois = service.findNearby(x, y, dmax);
        return ResponseEntity.ok(nearbyPois);
    }
}
