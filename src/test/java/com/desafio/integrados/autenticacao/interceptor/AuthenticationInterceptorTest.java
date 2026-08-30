package com.desafio.integrados.autenticacao.interceptor;

import com.desafio.integrados.autenticacao.annotation.PublicEndpoint;
import com.desafio.integrados.autenticacao.exception.InvalidTokenException;
import com.desafio.integrados.autenticacao.service.TokenValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationInterceptorTest {

    @Mock
    private TokenValidationService tokenValidationService;

    private AuthenticationInterceptor authenticationInterceptor;

    @BeforeEach
    void setUp() {
        authenticationInterceptor = new AuthenticationInterceptor(tokenValidationService);
    }

    @Test
    @DisplayName("Deve permitir requisicoes HTTP OPTIONS (CORS preflight)")
    void shouldAllowOptionsRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/foo-bar");
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean result = authenticationInterceptor.preHandle(request, response, new Object());

        assertTrue(result);
        verifyNoInteractions(tokenValidationService);
    }

    @Test
    @DisplayName("Deve permitir requisicao para metodo anotado com @PublicEndpoint")
    void shouldAllowPublicEndpointMethod() throws NoSuchMethodException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/recursos/publico");
        MockHttpServletResponse response = new MockHttpServletResponse();

        TestPublicController controller = new TestPublicController();
        Method method = TestPublicController.class.getMethod("publicMethod");
        HandlerMethod handlerMethod = new HandlerMethod(controller, method);

        boolean result = authenticationInterceptor.preHandle(request, response, handlerMethod);

        assertTrue(result);
        verifyNoInteractions(tokenValidationService);
    }

    @Test
    @DisplayName("Deve permitir requisicao com token valido")
    void shouldAllowRequestWithValidToken() throws NoSuchMethodException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo-bar");
        request.addHeader(HttpHeaders.AUTHORIZATION, "vYQIYxOpyfr==");
        MockHttpServletResponse response = new MockHttpServletResponse();

        TestProtectedController controller = new TestProtectedController();
        Method method = TestProtectedController.class.getMethod("protectedMethod");
        HandlerMethod handlerMethod = new HandlerMethod(controller, method);

        when(tokenValidationService.isValid("vYQIYxOpyfr==")).thenReturn(true);

        boolean result = authenticationInterceptor.preHandle(request, response, handlerMethod);

        assertTrue(result);
        verify(tokenValidationService, times(1)).isValid("vYQIYxOpyfr==");
    }

    @Test
    @DisplayName("Deve lancar InvalidTokenException quando o header Authorization estiver ausente")
    void shouldThrowExceptionWhenAuthorizationHeaderIsMissing() throws NoSuchMethodException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo-bar");
        MockHttpServletResponse response = new MockHttpServletResponse();

        TestProtectedController controller = new TestProtectedController();
        Method method = TestProtectedController.class.getMethod("protectedMethod");
        HandlerMethod handlerMethod = new HandlerMethod(controller, method);

        InvalidTokenException exception = assertThrows(
                InvalidTokenException.class,
                () -> authenticationInterceptor.preHandle(request, response, handlerMethod)
        );

        assertEquals("Cabeçalho 'Authorization' ausente ou vazio.", exception.getMessage());
        verifyNoInteractions(tokenValidationService);
    }

    @Test
    @DisplayName("Deve lancar InvalidTokenException quando o token for invalido")
    void shouldThrowExceptionWhenTokenIsInvalid() throws NoSuchMethodException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo-bar");
        request.addHeader(HttpHeaders.AUTHORIZATION, "token-invalido");
        MockHttpServletResponse response = new MockHttpServletResponse();

        TestProtectedController controller = new TestProtectedController();
        Method method = TestProtectedController.class.getMethod("protectedMethod");
        HandlerMethod handlerMethod = new HandlerMethod(controller, method);

        when(tokenValidationService.isValid("token-invalido")).thenReturn(false);

        InvalidTokenException exception = assertThrows(
                InvalidTokenException.class,
                () -> authenticationInterceptor.preHandle(request, response, handlerMethod)
        );

        assertEquals("Token de autorização inválido ou expirado.", exception.getMessage());
        verify(tokenValidationService, times(1)).isValid("token-invalido");
    }

    static class TestProtectedController {
        public void protectedMethod() {}
    }

    static class TestPublicController {
        @PublicEndpoint
        public void publicMethod() {}
    }
}
