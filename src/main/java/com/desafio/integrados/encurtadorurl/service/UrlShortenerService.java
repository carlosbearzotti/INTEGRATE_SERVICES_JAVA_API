package com.desafio.integrados.encurtadorurl.service;

public interface UrlShortenerService {

    String shortenUrl(String originalUrl, String baseUrl);

    String getOriginalUrlAndTrackAccess(String shortCode);
}
