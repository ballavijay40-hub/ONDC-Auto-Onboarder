package com.ondc.ai;

import com.ondc.dto.request.AiGenerateRequest;
import com.ondc.dto.response.AiCatalogResponse;
import com.ondc.dto.response.ComplianceResponse;
import com.ondc.entity.Product;
import com.ondc.entity.Seller;
import com.ondc.entity.UploadedDocument;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceRouter {

    private final GeminiService geminiService;
    private final ClaudeService claudeService;
    private final CatalogGenerator catalogGenerator;
    private final ComplianceAI complianceAI;

    public AiCatalogResponse generateCatalog(AiGenerateRequest request) {
        String prompt = PromptBuilder.buildCatalogPrompt(request);
        String preferred = request.getProvider() != null ? request.getProvider().toLowerCase() : "gemini";

        String rawResponse = null;
        String providerUsed = null;

        if ("claude".equals(preferred)) {
            if (claudeService.isConfigured()) {
                rawResponse = claudeService.generateContent(prompt);
                if (rawResponse != null) providerUsed = "Anthropic Claude";
            }
            if (rawResponse == null && geminiService.isConfigured()) {
                rawResponse = geminiService.generateContent(prompt);
                if (rawResponse != null) providerUsed = "Google Gemini (Fallback)";
            }
        } else {
            // Default to Gemini for Catalog Generation
            if (geminiService.isConfigured()) {
                rawResponse = geminiService.generateContent(prompt);
                if (rawResponse != null) providerUsed = "Google Gemini";
            }
            if (rawResponse == null && claudeService.isConfigured()) {
                rawResponse = claudeService.generateContent(prompt);
                if (rawResponse != null) providerUsed = "Anthropic Claude (Fallback)";
            }
        }

        if (providerUsed == null) {
            providerUsed = "Local Intelligent Rule Engine";
        }

        return catalogGenerator.parseOrFallback(rawResponse, request, providerUsed);
    }

    public ComplianceResponse auditCompliance(Seller seller,
                                            List<UploadedDocument> documents,
                                            List<Product> products,
                                            String preferredProvider) {
        String prompt = PromptBuilder.buildComplianceAuditPrompt(seller, documents, products);
        String preferred = preferredProvider != null ? preferredProvider.toLowerCase() : "claude";

        String rawResponse = null;
        String providerUsed = null;

        if ("gemini".equals(preferred)) {
            if (geminiService.isConfigured()) {
                rawResponse = geminiService.generateContent(prompt);
                if (rawResponse != null) providerUsed = "Google Gemini";
            }
            if (rawResponse == null && claudeService.isConfigured()) {
                rawResponse = claudeService.generateContent(prompt);
                if (rawResponse != null) providerUsed = "Anthropic Claude (Fallback)";
            }
        } else {
            // Default to Claude for Compliance Auditing
            if (claudeService.isConfigured()) {
                rawResponse = claudeService.generateContent(prompt);
                if (rawResponse != null) providerUsed = "Anthropic Claude";
            }
            if (rawResponse == null && geminiService.isConfigured()) {
                rawResponse = geminiService.generateContent(prompt);
                if (rawResponse != null) providerUsed = "Google Gemini (Fallback)";
            }
        }

        return complianceAI.evaluateCompliance(seller, documents, products, rawResponse, providerUsed);
    }
}
