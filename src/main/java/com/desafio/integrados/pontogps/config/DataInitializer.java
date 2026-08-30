package com.desafio.integrados.pontogps.config;

import com.desafio.integrados.pontogps.domain.PointOfInterest;
import com.desafio.integrados.pontogps.repository.PointOfInterestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Inicializador para carregar a base de dados de exemplo fornecida no enunciado do desafio.
 */
@Configuration
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final PointOfInterestRepository repository;

    @Value("${app.poi.seed-sample-data:true}")
    private boolean seedSampleData;

    public DataInitializer(PointOfInterestRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (!seedSampleData || repository.count() > 0) {
            return;
        }

        log.info("Inicializando dados de exemplo da XY Inc...");

        List<PointOfInterest> samplePois = List.of(
                new PointOfInterest("Lanchonete", 27, 12),
                new PointOfInterest("Posto", 31, 18),
                new PointOfInterest("Joalheria", 15, 12),
                new PointOfInterest("Floricultura", 19, 21),
                new PointOfInterest("Pub", 12, 8),
                new PointOfInterest("Supermercado", 23, 6),
                new PointOfInterest("Churrascaria", 28, 2)
        );

        repository.saveAll(java.util.Objects.requireNonNull(samplePois));
        log.info("Base de dados de exemplo carregada com {} POIs.", samplePois.size());
    }
}
