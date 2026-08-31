package com.desafio.integrados.admin.repository;

import com.desafio.integrados.admin.domain.Consumer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsumerRepository extends JpaRepository<Consumer, Long> {
    Optional<Consumer> findByApiKey(String apiKey);
    Optional<Consumer> findBySchemaName(String schemaName);
    boolean existsBySchemaName(String schemaName);
}
