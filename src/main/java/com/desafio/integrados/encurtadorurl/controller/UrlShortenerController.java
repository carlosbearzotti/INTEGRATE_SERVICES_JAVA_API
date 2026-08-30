package com.desafio.integrados.encurtadorurl.controller;

import com.desafio.integrados.encurtadorurl.dto.ShortenUrlRequest;
import com.desafio.integrados.encurtadorurl.dto.ShortenUrlResponse;
import com.desafio.integrados.encurtadorurl.service.UrlShortenerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Objects;

@RestController
public class UrlShortenerController {

    private final UrlShortenerService urlShortenerService;

    public UrlShortenerController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @PostMapping({"/shorten-url", "/api/shorten-url"})
    public ResponseEntity<ShortenUrlResponse> shortenUrl(
            @Valid @RequestBody ShortenUrlRequest request,
            @NonNull HttpServletRequest httpServletRequest) {

        String baseUrl = ServletUriComponentsBuilder.fromRequestUri(Objects.requireNonNull(httpServletRequest))
                .replacePath(null)
                .build()
                .toUriString();

        String shortenedUrl = urlShortenerService.shortenUrl(request.url(), baseUrl);
        return ResponseEntity.ok(new ShortenUrlResponse(shortenedUrl));
    }

    @GetMapping("/{shortCode:[a-zA-Z0-9]{5,10}}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortCode) {
        String originalUrl = urlShortenerService.getOriginalUrlAndTrackAccess(shortCode);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(Objects.requireNonNull(URI.create(originalUrl)))
                .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                .build();
    }
}
