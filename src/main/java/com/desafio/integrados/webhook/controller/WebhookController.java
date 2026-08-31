package com.desafio.integrados.webhook.controller;

import com.desafio.integrados.autenticacao.annotation.PublicEndpoint;
import com.desafio.integrados.webhook.dto.WebhookSubscriptionRequest;
import com.desafio.integrados.webhook.dto.WebhookTestDispatchRequest;
import com.desafio.integrados.webhook.model.WebhookDelivery;
import com.desafio.integrados.webhook.model.WebhookSubscription;
import com.desafio.integrados.webhook.service.WebhookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    private final WebhookService webhookService;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/subscriptions")
    @PublicEndpoint
    public ResponseEntity<WebhookSubscription> subscribe(@Valid @RequestBody WebhookSubscriptionRequest request) {
        WebhookSubscription subscription = webhookService.subscribe(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
    }

    @GetMapping("/subscriptions")
    @PublicEndpoint
    public ResponseEntity<List<WebhookSubscription>> listSubscriptions() {
        return ResponseEntity.ok(webhookService.listSubscriptions());
    }

    @GetMapping("/deliveries")
    @PublicEndpoint
    public ResponseEntity<List<WebhookDelivery>> listDeliveries() {
        return ResponseEntity.ok(webhookService.listDeliveries());
    }

    @PostMapping("/test-dispatch")
    @PublicEndpoint
    public ResponseEntity<WebhookDelivery> testDispatch(@Valid @RequestBody WebhookTestDispatchRequest request) {
        return ResponseEntity.ok(webhookService.dispatchTestEvent(request));
    }
}
