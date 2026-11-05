package com.ondc.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ondc.dto.request.AiGenerateRequest;
import com.ondc.dto.response.AiCatalogResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CatalogGenerator {

    private final ObjectMapper objectMapper;

    public AiCatalogResponse parseOrFallback(String rawAiResponse, AiGenerateRequest request, String providerUsed) {
        if (rawAiResponse != null && !rawAiResponse.isBlank()) {
            try {
                String cleaned = cleanJsonString(rawAiResponse);
                JsonNode node = objectMapper.readTree(cleaned);

                List<String> tags = new ArrayList<>();
                if (node.has("tags") && node.get("tags").isArray()) {
                    node.get("tags").forEach(t -> tags.add(t.asText()));
                }

                List<String> highlights = new ArrayList<>();
                if (node.has("highlights") && node.get("highlights").isArray()) {
                    node.get("highlights").forEach(h -> highlights.add(h.asText()));
                }

                return AiCatalogResponse.builder()
                        .title(node.path("title").asText(request.getProductName()))
                        .description(node.path("description").asText("Standard e-commerce catalog item for " + request.getProductName()))
                        .category(node.path("category").asText(inferCategory(request)))
                        .brand(node.path("brand").asText(request.getBrand() != null ? request.getBrand() : "Generic"))
                        .color(node.path("color").asText(request.getColor() != null ? request.getColor() : "Multi"))
                        .size(node.path("size").asText("Free Size"))
                        .gender(node.path("gender").asText(request.getGender() != null ? request.getGender() : "Unisex"))
                        .material(node.path("material").asText(request.getMaterial() != null ? request.getMaterial() : "Cotton Blend"))
                        .careInstructions(node.path("careInstructions").asText("Hand wash or machine wash gentle"))
                        .tags(tags.isEmpty() ? defaultTags(request) : tags)
                        .highlights(highlights)
                        .providerUsed(providerUsed)
                        .fallbackUsed(false)
                        .build();
            } catch (Exception ex) {
                log.warn("Failed to parse AI JSON response, falling back to deterministic template: {}", ex.getMessage());
            }
        }

        return generateFallback(request);
    }

    public AiCatalogResponse generateFallback(AiGenerateRequest request) {
        String name = request.getProductName().trim();
        String brand = request.getBrand() != null && !request.getBrand().isBlank() ? request.getBrand() : "Standard";
        String color = request.getColor() != null && !request.getColor().isBlank() ? request.getColor() : "Classic";
        String material = request.getMaterial() != null && !request.getMaterial().isBlank() ? request.getMaterial() : "Premium Cotton";
        String gender = request.getGender() != null && !request.getGender().isBlank() ? request.getGender() : "Unisex";
        String category = inferCategory(request);

        String title = String.format("%s %s %s (%s)", brand, color, name, gender);
        if (title.length() > 80) {
            title = title.substring(0, 77) + "...";
        }

        String description = String.format(
                "Elevate your daily fashion with this %s crafted from %s. Designed for %s with a comfortable contemporary fit, excellent durability, and breathable all-day wear.",
                name.toLowerCase(), material.toLowerCase(), gender.toLowerCase()
        );

        return AiCatalogResponse.builder()
                .title(title)
                .description(description)
                .category(category)
                .brand(brand)
                .color(color)
                .size("M")
                .gender(gender)
                .material(material)
                .careInstructions("Machine wash cold with like colors. Do not bleach. Warm iron if needed.")
                .tags(defaultTags(request))
                .highlights(List.of("Comfortable fit", "Breathable " + material, "ONDC standard compliant"))
                .providerUsed("Rule-Based Fallback")
                .fallbackUsed(true)
                .build();
    }

    private String cleanJsonString(String raw) {
        String s = raw.trim();
        if (s.startsWith("```json")) {
            s = s.substring(7);
        } else if (s.startsWith("```")) {
            s = s.substring(3);
        }
        if (s.endsWith("```")) {
            s = s.substring(0, s.length() - 3);
        }
        return s.trim();
    }

    private String inferCategory(AiGenerateRequest req) {
        if (req.getCategoryHint() != null && !req.getCategoryHint().isBlank()) {
            return req.getCategoryHint();
        }
        String lower = req.getProductName().toLowerCase();
        if (lower.contains("shirt") || lower.contains("tshirt") || lower.contains("t-shirt") || lower.contains("top")) {
            return "Apparel & Accessories > Tops & Shirts";
        } else if (lower.contains("jean") || lower.contains("trouser") || lower.contains("pant")) {
            return "Apparel & Accessories > Bottoms & Pants";
        } else if (lower.contains("shoe") || lower.contains("sneaker") || lower.contains("boot") || lower.contains("sandal")) {
            return "Apparel & Accessories > Footwear";
        } else if (lower.contains("saree") || lower.contains("kurta") || lower.contains("lehenga")) {
            return "Apparel & Accessories > Ethnic Wear";
        } else if (lower.contains("bag") || lower.contains("belt") || lower.contains("wallet")) {
            return "Apparel & Accessories > Fashion Accessories";
        }
        return "Apparel & Accessories > General";
    }

    private List<String> defaultTags(AiGenerateRequest req) {
        List<String> tags = new ArrayList<>();
        tags.add("fashion");
        tags.add("apparel");
        tags.add("ondc");
        if (req.getColor() != null) tags.add(req.getColor().toLowerCase());
        if (req.getBrand() != null) tags.add(req.getBrand().toLowerCase());
        return tags;
    }
}

