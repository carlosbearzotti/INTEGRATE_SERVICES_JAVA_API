package com.desafio.integrados.criptografia.service;

import com.desafio.integrados.criptografia.domain.Transaction;
import com.desafio.integrados.criptografia.dto.TransactionDTO;
import com.desafio.integrados.criptografia.repository.TransactionRepository;
import com.desafio.integrados.usuario.domain.User;
import com.desafio.integrados.usuario.repository.UserRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository repository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public TransactionDTO create(TransactionDTO dto) {
        User user = null;
        Long dtoUserId = dto.getUserId();
        if (dtoUserId != null) {
            user = userRepository.findById(dtoUserId).orElse(null);
        }

        Transaction entity = new Transaction(user, dto.getUserDocument(), dto.getCreditCardToken(), dto.getValue());
        entity = repository.save(entity);
        return mapToDTO(entity);
    }

    public TransactionDTO findById(@NonNull Long id) {
        return repository.findById(Objects.requireNonNull(id))
                .map(this::mapToDTO)
                .orElse(null);
    }

    public List<TransactionDTO> findAll() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> findByUserId(Long userId) {
        return repository.findByUserId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public TransactionDTO update(@NonNull Long id, TransactionDTO dto) {
        return repository.findById(Objects.requireNonNull(id)).map(existing -> {
            existing.setUserDocument(dto.getUserDocument());
            existing.setCreditCardToken(dto.getCreditCardToken());
            existing.setValue(dto.getValue());
            Long dtoUserId = dto.getUserId();
            if (dtoUserId != null) {
                User user = userRepository.findById(dtoUserId).orElse(null);
                existing.setUser(user);
            }
            return mapToDTO(repository.save(existing));
        }).orElse(null);
    }

    public void delete(@NonNull Long id) {
        repository.deleteById(Objects.requireNonNull(id));
    }

    private TransactionDTO mapToDTO(Transaction entity) {
        Long userId = entity.getUser() != null ? entity.getUser().getId() : null;
        return new TransactionDTO(
                entity.getId(),
                userId,
                entity.getUserDocument(),
                entity.getCreditCardToken(),
                entity.getValue()
        );
    }
}
