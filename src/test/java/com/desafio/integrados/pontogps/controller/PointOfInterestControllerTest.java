package com.desafio.integrados.pontogps.controller;

import com.desafio.integrados.pontogps.dto.CreatePoiRequest;
import com.desafio.integrados.pontogps.dto.NearbyPoiResponse;
import com.desafio.integrados.pontogps.dto.PoiResponse;
import com.desafio.integrados.pontogps.exception.InvalidCoordinateException;
import com.desafio.integrados.pontogps.exception.PointOfInterestExceptionHandler;
import com.desafio.integrados.pontogps.service.PointOfInterestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("null")
@WebMvcTest(PointOfInterestController.class)
@Import({PointOfInterestExceptionHandler.class, com.desafio.integrados.autenticacao.config.SecurityConfig.class})
@DisplayName("Testes de Integração do Controller PointOfInterestController")
class PointOfInterestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PointOfInterestService service;

    @Test
    @DisplayName("POST /pois - Deve criar POI com sucesso e retornar HTTP 201 Created")
    void shouldCreatePoiSuccessfully() throws Exception {
        CreatePoiRequest request = new CreatePoiRequest("Lanchonete", 27, 12);
        PoiResponse response = new PoiResponse(1L, "Lanchonete", 27, 12);

        when(service.create(any(CreatePoiRequest.class))).thenReturn(response);

        mockMvc.perform(post("/pois")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Lanchonete")))
                .andExpect(jsonPath("$.x", is(27)))
                .andExpect(jsonPath("$.y", is(12)));
    }

    @Test
    @DisplayName("POST /pois - Deve aceitar JSON em português ('nome', 'x', 'y')")
    void shouldCreatePoiWithPortugueseFields() throws Exception {
        String requestJson = """
                {
                    "nome": "Posto",
                    "x": 31,
                    "y": 18
                }
                """;
        PoiResponse response = new PoiResponse(2L, "Posto", 31, 18);

        when(service.create(any(CreatePoiRequest.class))).thenReturn(response);

        mockMvc.perform(post("/pois")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Posto")))
                .andExpect(jsonPath("$.x", is(31)))
                .andExpect(jsonPath("$.y", is(18)));
    }

    @Test
    @DisplayName("POST /pois - Deve retornar HTTP 400 Bad Request para nome em branco")
    void shouldReturnBadRequestForBlankName() throws Exception {
        String invalidJson = """
                {
                    "name": "   ",
                    "x": 10,
                    "y": 10
                }
                """;

        mockMvc.perform(post("/pois")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Error")));
    }

    @Test
    @DisplayName("POST /pois - Deve retornar HTTP 400 Bad Request para coordenadas negativas")
    void shouldReturnBadRequestForNegativeCoordinates() throws Exception {
        String invalidJson = """
                {
                    "name": "Parque",
                    "x": -5,
                    "y": 10
                }
                """;

        mockMvc.perform(post("/pois")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Error")));
    }

    @Test
    @DisplayName("GET /pois - Deve listar todos os POIs e retornar HTTP 200 OK")
    void shouldListAllPois() throws Exception {
        List<PoiResponse> pois = List.of(
                new PoiResponse(1L, "Lanchonete", 27, 12),
                new PoiResponse(2L, "Posto", 31, 18)
        );

        when(service.findAll()).thenReturn(pois);

        mockMvc.perform(get("/pois"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Lanchonete")))
                .andExpect(jsonPath("$[1].name", is("Posto")));
    }

    @Test
    @DisplayName("GET /pois/nearby - Deve retornar POIs próximos com HTTP 200 OK")
    void shouldReturnNearbyPois() throws Exception {
        List<NearbyPoiResponse> nearbyList = List.of(
                new NearbyPoiResponse(6L, "Supermercado", 23, 6, 5.0),
                new NearbyPoiResponse(3L, "Joalheria", 15, 12, 5.39),
                new NearbyPoiResponse(1L, "Lanchonete", 27, 12, 7.28),
                new NearbyPoiResponse(5L, "Pub", 12, 8, 8.25)
        );

        when(service.findNearby(20, 10, 10.0)).thenReturn(nearbyList);

        mockMvc.perform(get("/pois/nearby")
                        .param("x", "20")
                        .param("y", "10")
                        .param("dmax", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Supermercado", "Joalheria", "Lanchonete", "Pub")))
                .andExpect(jsonPath("$[0].distanceInMeters", is(5.0)));
    }

    @Test
    @DisplayName("GET /pois/proximidade - Deve funcionar como alias para /pois/nearby")
    void shouldWorkWithProximidadeEndpointAlias() throws Exception {
        List<NearbyPoiResponse> nearbyList = List.of(
                new NearbyPoiResponse(6L, "Supermercado", 23, 6, 5.0)
        );

        when(service.findNearby(20, 10, 10.0)).thenReturn(nearbyList);

        mockMvc.perform(get("/pois/proximidade")
                        .param("x", "20")
                        .param("y", "10")
                        .param("dmax", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Supermercado")));
    }

    @Test
    @DisplayName("GET /pois/nearby - Deve retornar HTTP 400 quando parâmetros de coordenada forem inválidos")
    void shouldReturnBadRequestWhenCoordinatesAreInvalid() throws Exception {
        when(service.findNearby(-1, 10, 10.0))
                .thenThrow(new InvalidCoordinateException("As coordenadas de referência devem ser inteiros não negativos."));

        mockMvc.perform(get("/pois/nearby")
                        .param("x", "-1")
                        .param("y", "10")
                        .param("dmax", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", is("As coordenadas de referência devem ser inteiros não negativos.")));
    }
}
