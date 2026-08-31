package com.desafio.integrados.webhook.repository;

import com.desafio.integrados.webhook.model.WebhookDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebhookDeliveryRepository extends JpaRepository<WebhookDelivery, Long> {
    List<WebhookDelivery> findByOrderByCreatedAtDesc();
    List<WebhookDelivery> findByWebhookIdOrderByCreatedAtDesc(Long webhookId);
}
