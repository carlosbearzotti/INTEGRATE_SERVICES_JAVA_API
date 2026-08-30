package com.desafio.integrados.autenticacao.service;

public interface TokenValidationService {

    /**
     * Valida se o cabeçalho ou token de autorização fornecido é válido.
     *
     * @param authorizationHeader valor do cabeçalho Authorization
     * @return true se o token for válido; false caso contrário
     */
    boolean isValid(String authorizationHeader);

    /**
     * Extrai e sanitiza o token contido no cabeçalho Authorization,
     * removendo eventuais prefixos como 'Bearer ' e espaços adicionais.
     *
     * @param authorizationHeader valor do cabeçalho Authorization
     * @return token extraído e limpo, ou null se nulo ou vazio
     */
    String extractToken(String authorizationHeader);
}
