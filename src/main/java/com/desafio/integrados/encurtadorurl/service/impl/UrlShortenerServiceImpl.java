package com.desafio.integrados.encurtadorurl.service.impl;

import com.desafio.integrados.encurtadorurl.config.UrlShortenerProperties;
import com.desafio.integrados.encurtadorurl.domain.UrlMapping;
import com.desafio.integrados.encurtadorurl.exception.InvalidUrlException;
import com.desafio.integrados.encurtadorurl.exception.UrlNotFoundException;
import com.desafio.integrados.encurtadorurl.repository.UrlMappingRepository;
import com.desafio.integrados.encurtadorurl.service.UrlShortenerService;
import com.desafio.integrados.encurtadorurl.util.ShortCodeGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

@Service
public class UrlShortenerServiceImpl implements UrlShortenerService {

    private static final int MAX_COLLISION_RETRIES = 10;

    private final UrlMappingRepository repository;
    private final ShortCodeGenerator codeGenerator;
    private final UrlShortenerProperties properties;

    public UrlShortenerServiceImpl(UrlMappingRepository repository,
                                   ShortCodeGenerator codeGenerator,
                                   UrlShortenerProperties properties) {
        this.repository = repository;
        this.codeGenerator = codeGenerator;
        this.properties = properties;
    }

    @Override
    @Transactional
    public String shortenUrl(String originalUrl, String requestBaseUrl) {
        validateUrl(originalUrl);

        String shortCode = generateUniqueShortCode();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusDays(properties.getExpirationDays());

        UrlMapping mapping = new UrlMapping(shortCode, originalUrl, now, expiresAt);
        repository.save(mapping);

        String effectiveBaseUrl = resolveBaseUrl(requestBaseUrl);
        return effectiveBaseUrl + "/" + shortCode;
    }

    @Override
    @Transactional
    public String getOriginalUrlAndTrackAccess(String shortCode) {
        if (shortCode == null || shortCode.isBlank()) {
            throw new UrlNotFoundException("Código de URL encurtada não informado.");
        }

        UrlMapping mapping = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("URL encurtada não encontrada para o código: " + shortCode));

        if (mapping.isExpired()) {
            throw new UrlNotFoundException("A URL encurtada expirou e não está mais disponível.");
        }

        mapping.incrementAccessCount();
        repository.save(mapping);

        return mapping.getOriginalUrl();
    }

    private String generateUniqueShortCode() {
        int length = properties.getCodeLength();
        for (int i = 0; i < MAX_COLLISION_RETRIES; i++) {
            String candidateCode = codeGenerator.generateCode(length);
            if (!repository.existsByShortCode(candidateCode)) {
                return candidateCode;
            }
        }
        throw new IllegalStateException("Não foi possível gerar um código único após múltiplas tentativas. Tente novamente.");
    }

    private void validateUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new InvalidUrlException("A URL não pode estar vazia.");
        }

        try {
            URI uri = new URI(url.trim());
            String scheme = uri.getScheme();
            if (scheme == null || (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))) {
                throw new InvalidUrlException("A URL deve conter o protocolo http ou https.");
            }
            if (uri.getHost() == null || uri.getHost().trim().isEmpty()) {
                throw new InvalidUrlException("A URL fornecida é inválida (sem domínio/host válido).");
            }
        } catch (URISyntaxException e) {
            throw new InvalidUrlException("A URL fornecida possui sintaxe inválida: " + e.getMessage());
        }
    }

    private String resolveBaseUrl(String requestBaseUrl) {
        String configuredBaseUrl = properties.getBaseUrl();
        String chosenBase = (configuredBaseUrl != null && !configuredBaseUrl.isBlank())
                ? configuredBaseUrl.trim()
                : (requestBaseUrl != null && !requestBaseUrl.isBlank()) ? requestBaseUrl.trim() : "http://localhost:8080";

        return chosenBase.replaceAll("/+$", "");
    }
}
