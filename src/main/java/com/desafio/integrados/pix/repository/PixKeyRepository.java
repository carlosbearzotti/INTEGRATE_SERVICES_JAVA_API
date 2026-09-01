package com.desafio.integrados.pix.repository;

import com.desafio.integrados.pix.domain.PixKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PixKeyRepository extends JpaRepository<PixKey, Long> {
    Optional<PixKey> findByKeyValue(String keyValue);
    List<PixKey> findByUserId(Long userId);
}
