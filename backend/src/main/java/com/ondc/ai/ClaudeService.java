package com.ondc.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ondc.config.AppProperties;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaudeService {

    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;

    public boolean isConfigured() {
        String key = appProperties.getAnthropic().getApiKey();
        return key != null && !key.isBlank();
    }

    public String generateContent(String prompt) {
        if (!isConfigured()) {
            log.info("Anthropic API key is not configured, skipping call");
            return null;
        }

        try {
            String apiKey = appProperties.getAnthropic().getApiKey();
            String model = appProperties.getAnthropic().getModel();
            String baseUrl = appProperties.getAnthropic().getBaseUrl();
            String version = appProperties.getAnthropic().getVersion();
            int timeout = appProperties.getAnthropic().getTimeoutSeconds();

            WebClient client = WebClient.builder()
                    .baseUrl(baseUrl)
                    .defaultHeader("x-api-key", apiKey)
                    .defaultHeader("anthropic-version", version != null ? version : "2023-06-01")
                    .defaultHeader("content-type", MediaType.APPLICATION_JSON_VALUE)
                    .build();

            Map<String, Object> body = Map.of(
                    "model", model,
                    "max_tokens", 1500,
                    "messages", List.of(
                            Map.of("role", "user", "content", prompt)
                    )
            );

            String responseBody = client.post()
                    .uri("/v1/messages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(timeout));

            return extractTextFromClaudeResponse(responseBody);
        } catch (Exception ex) {
            log.warn("Claude API call failed: {}", ex.getMessage());
            return null;
        }
    }

    private String extractTextFromClaudeResponse(String responseJson) {
        if (responseJson == null) return null;
        try {
            JsonNode root = objectMapper.readTree(responseJson);
            JsonNode content = root.path("content");
            if (content.isArray() && !content.isEmpty()) {
                return content.get(0).path("text").asText();
            }
        } catch (Exception e) {
            log.error("Failed to parse Claude response JSON", e);
        }
        return null;
    }
}
