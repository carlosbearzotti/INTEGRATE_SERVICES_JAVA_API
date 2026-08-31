package com.desafio.integrados.webhook.service;

import com.desafio.integrados.webhook.dto.WebhookSubscriptionRequest;
import com.desafio.integrados.webhook.dto.WebhookTestDispatchRequest;
import com.desafio.integrados.webhook.model.WebhookDelivery;
import com.desafio.integrados.webhook.model.WebhookSubscription;
import com.desafio.integrados.webhook.repository.WebhookDeliveryRepository;
import com.desafio.integrados.webhook.repository.WebhookSubscriptionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebhookServiceTest {

    @Mock
    private WebhookSubscriptionRepository subscriptionRepository;

    @Mock
    private WebhookDeliveryRepository deliveryRepository;

    @InjectMocks
    private WebhookService webhookService;

    @Test
    void shouldSubscribeSuccessfully() {
        when(subscriptionRepository.save(any(WebhookSubscription.class))).thenAnswer(i -> {
            WebhookSubscription sub = i.getArgument(0);
            sub.setId(1L);
            return sub;
        });

        WebhookSubscriptionRequest request = new WebhookSubscriptionRequest();
        request.setUrl("https://api.exemplo.com/webhook");
        request.setEventTypes("TRANSACTION.SETTLED,TRANSFER.SCHEDULED");
        request.setSecretKey("my_secret_123");

        WebhookSubscription subscription = webhookService.subscribe(request);

        assertNotNull(subscription);
        assertEquals(1L, subscription.getId());
        assertEquals("https://api.exemplo.com/webhook", subscription.getUrl());
        assertEquals("ACTIVE", subscription.getStatus());
        assertEquals("my_secret_123", subscription.getSecretKey());

        verify(subscriptionRepository, times(1)).save(any(WebhookSubscription.class));
    }

    @Test
    void shouldSubscribeWithGeneratedSecretKey() {
        when(subscriptionRepository.save(any(WebhookSubscription.class))).thenAnswer(i -> i.getArgument(0));

        WebhookSubscriptionRequest request = new WebhookSubscriptionRequest();
        request.setUrl("https://api.exemplo.com/webhook");
        request.setEventTypes("TRANSACTION.SETTLED");
        // Não fornecendo secretKey

        WebhookSubscription subscription = webhookService.subscribe(request);

        assertNotNull(subscription.getSecretKey());
        assertTrue(subscription.getSecretKey().startsWith("whsec_"));
    }

    @Test
    void shouldDispatchTestEventSuccessfully() {
        WebhookSubscription sub = new WebhookSubscription();
        sub.setId(1L);
        sub.setUrl("https://api.exemplo.com/webhook");
        sub.setSecretKey("test_secret");

        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(sub));
        when(deliveryRepository.save(any(WebhookDelivery.class))).thenAnswer(i -> i.getArgument(0));

        WebhookTestDispatchRequest request = new WebhookTestDispatchRequest();
        request.setSubscriptionId(1L);
        request.setEventType("TRANSACTION.SETTLED");
        request.setSamplePayload("{\"test\":\"data\"}");

        WebhookDelivery delivery = webhookService.dispatchTestEvent(request);

        assertNotNull(delivery);
        assertEquals(1L, delivery.getWebhookId());
        assertEquals("TRANSACTION.SETTLED", delivery.getEventType());
        assertEquals("{\"test\":\"data\"}", delivery.getPayload());
        assertTrue(delivery.getSignature().startsWith("sha256="));
        assertEquals(200, delivery.getStatusCode());
        assertTrue(delivery.getSuccess());

        verify(deliveryRepository, times(1)).save(any(WebhookDelivery.class));
    }

    @Test
    void shouldGenerateHmacSignatureCorrectly() {
        String data = "{\"test\":\"data\"}";
        String key = "secret123";

        String signature = webhookService.generateHmacSignature(data, key);

        assertNotNull(signature);
        assertFalse(signature.startsWith("signature_error_"));
        // Com base em testes conhecidos de hmac
        assertEquals(64, signature.length()); 
    }
}
