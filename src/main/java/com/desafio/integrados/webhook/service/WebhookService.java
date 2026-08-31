package com.desafio.integrados.webhook.service;

import com.desafio.integrados.webhook.dto.WebhookSubscriptionRequest;
import com.desafio.integrados.webhook.dto.WebhookTestDispatchRequest;
import com.desafio.integrados.webhook.model.WebhookDelivery;
import com.desafio.integrados.webhook.model.WebhookSubscription;
import com.desafio.integrados.webhook.repository.WebhookDeliveryRepository;
import com.desafio.integrados.webhook.repository.WebhookSubscriptionRepository;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
@SuppressWarnings("null")
public class WebhookService {

    private final WebhookSubscriptionRepository subscriptionRepository;
    private final WebhookDeliveryRepository deliveryRepository;

    public WebhookService(WebhookSubscriptionRepository subscriptionRepository, WebhookDeliveryRepository deliveryRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.deliveryRepository = deliveryRepository;
    }

    public WebhookSubscription subscribe(WebhookSubscriptionRequest req) {
        WebhookSubscription sub = new WebhookSubscription();
        sub.setUrl(req.getUrl());
        sub.setEventTypes(req.getEventTypes());
        sub.setSecretKey(req.getSecretKey() != null && !req.getSecretKey().isBlank()
                ? req.getSecretKey()
                : "whsec_" + UUID.randomUUID().toString().replace("-", ""));
        sub.setStatus("ACTIVE");
        sub.setCreatedAt(LocalDateTime.now());

        return subscriptionRepository.save(sub);
    }

    public List<WebhookSubscription> listSubscriptions() {
        return subscriptionRepository.findAll();
    }

    public List<WebhookDelivery> listDeliveries() {
        return deliveryRepository.findByOrderByCreatedAtDesc();
    }

    public WebhookDelivery dispatchTestEvent(WebhookTestDispatchRequest req) {
        WebhookSubscription sub = subscriptionRepository.findById(req.getSubscriptionId())
                .orElseThrow(() -> new RuntimeException("Subscrição de Webhook não encontrada: " + req.getSubscriptionId()));

        String payload = req.getSamplePayload() != null && !req.getSamplePayload().isBlank()
                ? req.getSamplePayload()
                : generateDefaultPayload(req.getEventType());

        String signature = generateHmacSignature(payload, sub.getSecretKey());

        WebhookDelivery delivery = new WebhookDelivery();
        delivery.setWebhookId(sub.getId());
        delivery.setEventType(req.getEventType());
        delivery.setPayload(payload);
        delivery.setSignature("sha256=" + signature);
        delivery.setStatusCode(200);
        delivery.setResponseBody("{\"status\":\"received\",\"ack_id\":\"ack_" + UUID.randomUUID().toString().substring(0, 8) + "\"}");
        delivery.setSuccess(true);
        delivery.setCreatedAt(LocalDateTime.now());

        return deliveryRepository.save(delivery);
    }

    public String generateHmacSignature(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hmacBytes);
        } catch (Exception e) {
            return "signature_error_" + e.getMessage();
        }
    }

    private String generateDefaultPayload(String eventType) {
        String timestamp = LocalDateTime.now().toString();
        return String.format(
                "{\"event\":\"%s\",\"timestamp\":\"%s\",\"data\":{\"transactionId\":\"tx_%s\",\"amount\":1500.00,\"currency\":\"BRL\",\"tenant\":\"tenant_fintech\",\"status\":\"SETTLED\"}}",
                eventType, timestamp, UUID.randomUUID().toString().substring(0, 8)
        );
    }
}
