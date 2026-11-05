package com.ondc.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ondc.config.AppProperties;
import java.time.Duration;
import java.util.Base64;
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
public class GeminiService {

    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;

    public boolean isConfigured() {
        String key = appProperties.getGemini().getApiKey();
        return key != null && !key.isBlank();
    }

    public String generateContent(String prompt) {
        if (!isConfigured()) {
            log.info("Gemini API key is not configured, skipping call");
            return null;
        }

        try {
            String apiKey = appProperties.getGemini().getApiKey();
            String model = appProperties.getGemini().getModel();
            String baseUrl = appProperties.getGemini().getBaseUrl();
            int timeout = appProperties.getGemini().getTimeoutSeconds();

            WebClient client = WebClient.builder()
                    .baseUrl(baseUrl)
                    .build();

            Map<String, Object> body = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(Map.of("text", prompt)))
                    )
            );

            String responseBody = client.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1beta/models/" + model + ":generateContent")
                            .queryParam("key", apiKey)
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(timeout));

            return extractTextFromGeminiResponse(responseBody);
        } catch (Exception ex) {
            log.warn("Gemini API call failed: {}", ex.getMessage());
            return null;
        }
    }

    public String generateContentWithImage(String prompt, byte[] imageBytes, String mimeType) {
        if (!isConfigured() || imageBytes == null || imageBytes.length == 0) {
            return generateContent(prompt);
        }

        try {
            String apiKey = appProperties.getGemini().getApiKey();
            String model = appProperties.getGemini().getModel();
            String baseUrl = appProperties.getGemini().getBaseUrl();
            int timeout = appProperties.getGemini().getTimeoutSeconds();

            WebClient client = WebClient.builder()
                    .baseUrl(baseUrl)
                    .build();

            String base64Data = Base64.getEncoder().encodeToString(imageBytes);

            Map<String, Object> body = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", prompt),
                                    Map.of("inline_data", Map.of(
                                            "mime_type", mimeType != null ? mimeType : "image/jpeg",
                                            "data", base64Data
                                    ))
                            ))
                    )
            );

            String responseBody = client.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1beta/models/" + model + ":generateContent")
                            .queryParam("key", apiKey)
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(timeout));

            return extractTextFromGeminiResponse(responseBody);
        } catch (Exception ex) {
            log.warn("Gemini multimodal API call failed: {}", ex.getMessage());
            return generateContent(prompt);
        }
    }

    private String extractTextFromGeminiResponse(String responseJson) {
        if (responseJson == null) return null;
        try {
            JsonNode root = objectMapper.readTree(responseJson);
            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && !candidates.isEmpty()) {
                JsonNode parts = candidates.get(0).path("content").path("parts");
                if (parts.isArray() && !parts.isEmpty()) {
                    return parts.get(0).path("text").asText();
                }
            }
        } catch (Exception e) {
            log.error("Failed to parse Gemini response JSON", e);
        }
        return null;
    }
}
