package com.desafio.integrados.multitenancy.filter;

import com.desafio.integrados.admin.domain.Consumer;
import com.desafio.integrados.admin.repository.ConsumerRepository;
import com.desafio.integrados.multitenancy.context.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TenantFilter extends OncePerRequestFilter {

    private static final String TENANT_HEADER = "X-Tenant-ID";
    private static final String API_KEY_HEADER = "X-API-Key";

    private final ObjectProvider<ConsumerRepository> consumerRepositoryProvider;
    private final Map<String, String> apiKeyToSchemaCache = new ConcurrentHashMap<>();

    public TenantFilter(ObjectProvider<ConsumerRepository> consumerRepositoryProvider) {
        this.consumerRepositoryProvider = consumerRepositoryProvider;
    }

    @Override
    protected void doFilterInternal(@org.springframework.lang.NonNull HttpServletRequest request,
                                    @org.springframework.lang.NonNull HttpServletResponse response,
                                    @org.springframework.lang.NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Endpoints administrativos operam no schema public
        if (path.startsWith("/api/v1/admin") || path.startsWith("/swagger") || path.startsWith("/v3/api-docs")) {
            TenantContext.setTenantId("public");
        } else {
            String apiKey = request.getHeader(API_KEY_HEADER);
            String tenantId = request.getHeader(TENANT_HEADER);

            if (apiKey != null && !apiKey.isBlank()) {
                String cachedSchema = apiKeyToSchemaCache.get(apiKey);
                if (cachedSchema != null) {
                    TenantContext.setTenantId(cachedSchema);
                } else {
                    ConsumerRepository consumerRepository = consumerRepositoryProvider.getIfAvailable();
                    if (consumerRepository != null) {
                        Optional<Consumer> consumerOpt = consumerRepository.findByApiKey(apiKey);
                        if (consumerOpt.isPresent()) {
                            String schema = consumerOpt.get().getSchemaName();
                            apiKeyToSchemaCache.put(apiKey, schema);
                            TenantContext.setTenantId(schema);
                        }
                    }
                }
            } else if (tenantId != null && !tenantId.isBlank()) {
                TenantContext.setTenantId(tenantId);
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
