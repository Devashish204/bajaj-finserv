package com.example.webhook.service;

import com.example.webhook.response.WebhookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Slf4j
public class WebhookService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String INIT_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
    private static final String NAME = "Devashish Rajput";
    private static final String EMAIL = "devashish.rajput0907@gmail.com";
    private static final String REG_NO = "1032230486";

    public void initiateWebhookFlow() {
        // 1. Send POST to generate webhook
        Map<String, String> payload = Map.of(
                "name", NAME,
                "email", EMAIL,
                "regNo", REG_NO
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<WebhookResponse> response = restTemplate.postForEntity(INIT_URL, entity, WebhookResponse.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            WebhookResponse webhookData = response.getBody();

            log.info("Received webhook: {}", webhookData.getWebhook());
            log.info("Received accessToken: {}", webhookData.getAccessToken());

            // 2. Solve the SQL problem (Odd regNo = Question 1)
            String finalQuery = """
                SELECT d.DEPARTMENT_NAME, e.FIRST_NAME, SUM(p.AMOUNT) AS TOTAL_PAYMENT
                FROM EMPLOYEE e
                JOIN PAYMENTS p ON e.EMP_ID = p.EMP_ID
                JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID
                GROUP BY d.DEPARTMENT_NAME, e.FIRST_NAME
                ORDER BY TOTAL_PAYMENT DESC
                LIMIT 1
            """;

            // 3. Submit SQL query using JWT
            submitFinalQuery(webhookData.getWebhook(), webhookData.getAccessToken(), finalQuery);
        } else {
            log.error("Failed to receive a valid webhook response. Status: {}", response.getStatusCode());
        }
    }

    private void submitFinalQuery(String webhookUrl, String jwtToken, String query) {
        log.info("Submitting final query to webhook...");
        Map<String, String> body = Map.of("finalQuery", query);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", jwtToken);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(webhookUrl, entity, String.class);
if(response.getStatusCode().is2xxSuccessful()) {
    log.info("Submitted final query to webhook");
} else {
    log.error("Failed to receive a valid webhook response. Status: {}", response.getStatusCode());
}
        System.out.println("Submission response: " + response.getStatusCode());
    }
}

