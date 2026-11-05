package com.ondc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ondc.ai.CatalogGenerator;
import com.ondc.dto.request.AiGenerateRequest;
import com.ondc.dto.response.AiCatalogResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CatalogGeneratorTest {

    private CatalogGenerator catalogGenerator;

    @BeforeEach
    void setUp() {
        catalogGenerator = new CatalogGenerator(new ObjectMapper());
    }

    @Test
    void testFallbackGeneration() {
        AiGenerateRequest request = AiGenerateRequest.builder()
                .productName("Sports Running Shoe")
                .brand("AeroFit")
                .color("Navy Blue")
                .gender("Men")
                .material("Breathable Mesh")
                .build();

        AiCatalogResponse response = catalogGenerator.generateFallback(request);

        assertNotNull(response);
        assertTrue(response.isFallbackUsed());
        assertTrue(response.getTitle().contains("AeroFit"));
        assertTrue(response.getTitle().contains("Sports Running Shoe"));
        assertNotNull(response.getDescription());
        assertEquals("Apparel & Accessories > Footwear", response.getCategory());
        assertFalse(response.getTags().isEmpty());
    }

    @Test
    void testParseJsonResponse() {
        AiGenerateRequest request = AiGenerateRequest.builder()
                .productName("Classic Denim Jacket")
                .build();

        String rawJson = "{\"title\":\"Classic Denim Jacket\",\"description\":\"Vintage washed denim jacket with metal button closures.\",\"category\":\"Apparel & Accessories > Outerwear\",\"brand\":\"UrbanDenim\",\"color\":\"Indigo\",\"tags\":[\"denim\",\"jacket\",\"vintage\"]}";

        AiCatalogResponse response = catalogGenerator.parseOrFallback(rawJson, request, "Google Gemini");

        assertNotNull(response);
        assertFalse(response.isFallbackUsed());
        assertEquals("Google Gemini", response.getProviderUsed());
        assertEquals("Classic Denim Jacket", response.getTitle());
        assertEquals("UrbanDenim", response.getBrand());
        assertEquals(3, response.getTags().size());
    }
}
