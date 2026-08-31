package com.desafio.integrados.multitenancy;

import com.desafio.integrados.admin.domain.Consumer;
import com.desafio.integrados.admin.repository.ConsumerRepository;
import com.desafio.integrados.multitenancy.context.TenantContext;
import com.desafio.integrados.multitenancy.filter.TenantFilter;
import com.desafio.integrados.multitenancy.hibernate.TenantIdentifierResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MultiTenancyTest {

    @Mock
    private ConsumerRepository consumerRepository;

    @Mock
    private ObjectProvider<ConsumerRepository> consumerRepositoryProvider;

    @Mock
    private FilterChain filterChain;

    private TenantFilter tenantFilter;
    private TenantIdentifierResolver tenantIdentifierResolver;

    @BeforeEach
    void setUp() {
        lenient().when(consumerRepositoryProvider.getIfAvailable()).thenReturn(consumerRepository);
        tenantFilter = new TenantFilter(consumerRepositoryProvider);
        tenantIdentifierResolver = new TenantIdentifierResolver();
        TenantContext.clear();
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void shouldResolveDefaultTenantWhenNoHeaderPresent() {
        assertEquals("tenant_fintech", tenantIdentifierResolver.resolveCurrentTenantIdentifier());
    }

    @Test
    void shouldResolveTenantFromHeader() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/users");
        request.addHeader("X-Tenant-ID", "tenant_custom");
        MockHttpServletResponse response = new MockHttpServletResponse();

        doAnswer(invocation -> {
            assertEquals("tenant_custom", TenantContext.getTenantId());
            assertEquals("tenant_custom", tenantIdentifierResolver.resolveCurrentTenantIdentifier());
            return null;
        }).when(filterChain).doFilter(request, response);

        tenantFilter.doFilter(request, response, filterChain);

        // After request, context should be cleared
        assertEquals("tenant_fintech", TenantContext.getTenantId());
    }

    @Test
    void shouldResolveTenantFromApiKey() throws ServletException, IOException {
        String apiKey = "ak_test123456";
        Consumer consumer = new Consumer("Fintech Test", "tenant_fintech_test", apiKey);

        when(consumerRepository.findByApiKey(apiKey)).thenReturn(Optional.of(consumer));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/users");
        request.addHeader("X-API-Key", apiKey);
        MockHttpServletResponse response = new MockHttpServletResponse();

        doAnswer(invocation -> {
            assertEquals("tenant_fintech_test", TenantContext.getTenantId());
            return null;
        }).when(filterChain).doFilter(request, response);

        tenantFilter.doFilter(request, response, filterChain);
    }
}
