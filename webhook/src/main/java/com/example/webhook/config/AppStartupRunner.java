package com.example.webhook.config;

import com.example.webhook.service.WebhookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AppStartupRunner implements CommandLineRunner {

    private final WebhookService webhookService;

    public AppStartupRunner(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @Override
    public void run(String... args) {
        log.info("Application started. Initiating webhook generation...");
        webhookService.initiateWebhookFlow();
    }
}

