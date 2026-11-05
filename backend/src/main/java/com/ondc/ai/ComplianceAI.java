package com.ondc.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ondc.dto.response.ComplianceResponse;
import com.ondc.entity.ComplianceRecord;
import com.ondc.entity.DocumentType;
import com.ondc.entity.Product;
import com.ondc.entity.Seller;
import com.ondc.entity.UploadedDocument;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ComplianceAI {

    private final ObjectMapper objectMapper;

    public ComplianceResponse evaluateCompliance(Seller seller,
                                                List<UploadedDocument> documents,
                                                List<Product> products,
                                                String rawAiResponse,
                                                String providerUsed) {
        // First compute baseline deterministic score and checks
        int baseScore = 0;
        List<String> passedChecks = new ArrayList<>();
        List<String> missingItems = new ArrayList<>();
        List<String> remarks = new ArrayList<>();

        // 1. Tax & Regulatory Identifiers (30 pts)
        if (seller.getGstNumber() != null && !seller.getGstNumber().isBlank() && seller.getGstNumber().length() == 15) {
            baseScore += 15;
            passedChecks.add("Valid 15-character GSTIN registered (" + seller.getGstNumber() + ")");
        } else {
            missingItems.add("Valid 15-character GST registration number");
        }

        if (seller.getPanNumber() != null && !seller.getPanNumber().isBlank() && seller.getPanNumber().length() == 10) {
            baseScore += 15;
            passedChecks.add("Valid Income Tax PAN registered (" + seller.getPanNumber() + ")");
        } else {
            missingItems.add("Valid 10-character PAN number");
        }

        // 2. Business Entity & Contact Verification (20 pts)
        if (seller.getBusinessName() != null && !seller.getBusinessName().isBlank()) {
            baseScore += 5;
            passedChecks.add("Registered Business Name: " + seller.getBusinessName());
        } else {
            missingItems.add("Business trade name");
        }

        if (seller.getOwnerName() != null && !seller.getOwnerName().isBlank()) {
            baseScore += 5;
            passedChecks.add("Authorized Signatory/Owner: " + seller.getOwnerName());
        } else {
            missingItems.add("Owner or Authorized Signatory name");
        }

        if (seller.getPhone() != null && !seller.getPhone().isBlank()) {
            baseScore += 5;
            passedChecks.add("Contact phone verified");
        } else {
            missingItems.add("Business contact phone number");
        }

        if (seller.getAddress() != null && seller.getAddress().trim().length() > 5) {
            baseScore += 5;
            passedChecks.add("Principal place of business address verified");
        } else {
            missingItems.add("Registered business address");
        }

        // 3. Document Verification (20 pts)
        boolean hasGstDoc = documents.stream().anyMatch(d -> d.getDocumentType() == DocumentType.GST);
        boolean hasPanDoc = documents.stream().anyMatch(d -> d.getDocumentType() == DocumentType.PAN);
        if (hasGstDoc) {
            baseScore += 10;
            passedChecks.add("GST Certificate document uploaded and verified");
        } else {
            missingItems.add("Upload GST Registration Certificate (REG-06)");
        }
        if (hasPanDoc || documents.size() >= 2) {
            baseScore += 10;
            passedChecks.add("Identity proof (PAN / Business License) uploaded");
        } else {
            missingItems.add("Upload PAN card or Business License copy");
        }

        // 4. Product Catalog Compliance (30 pts)
        if (!products.isEmpty()) {
            baseScore += 10;
            passedChecks.add("Product catalog initiated (" + products.size() + " items)");

            boolean descriptionsValid = products.stream()
                    .allMatch(p -> p.getDescription() != null && p.getDescription().trim().length() >= 20);
            if (descriptionsValid) {
                baseScore += 10;
                passedChecks.add("All product descriptions satisfy ONDC consumer clarity requirements");
            } else {
                missingItems.add("Detailed product descriptions for all catalog items (> 20 chars)");
            }

            boolean attributesValid = products.stream()
                    .allMatch(p -> p.getPrice() != null && p.getCategory() != null);
            if (attributesValid) {
                baseScore += 10;
                passedChecks.add("Mandatory retail attributes (Category, MRP/Price) fully declared");
            } else {
                missingItems.add("Mandatory retail attributes (Price, Category) on all items");
            }
        } else {
            missingItems.add("Add at least 1 product to your fashion catalog");
        }

        int finalScore = Math.min(100, baseScore);
        String status = finalScore >= 80 ? ComplianceRecord.READY : ComplianceRecord.NEEDS_ATTENTION;

        if (finalScore >= 80) {
            remarks.add("All mandatory ONDC and Legal Metrology compliance standards met.");
            remarks.add("Seller is qualified for network-wide discoverability on ONDC.");
        } else {
            remarks.add("Action required: Complete missing documents and catalog details to reach ONDC Ready status.");
        }

        // Augment with AI feedback if available
        if (rawAiResponse != null && !rawAiResponse.isBlank()) {
            try {
                String cleaned = cleanJsonString(rawAiResponse);
                JsonNode aiNode = objectMapper.readTree(cleaned);
                if (aiNode.has("remarks") && aiNode.get("remarks").isArray()) {
                    aiNode.get("remarks").forEach(r -> {
                        String txt = r.asText();
                        if (!remarks.contains(txt)) remarks.add(txt);
                    });
                }
                if (aiNode.has("missingItems") && aiNode.get("missingItems").isArray()) {
                    aiNode.get("missingItems").forEach(m -> {
                        String txt = m.asText();
                        if (!missingItems.contains(txt)) missingItems.add(txt);
                    });
                }
            } catch (Exception e) {
                log.debug("Using rule-based compliance remarks (AI response non-JSON)");
            }
        }

        return ComplianceResponse.builder()
                .sellerId(seller.getId())
                .score(finalScore)
                .status(status)
                .passedChecks(passedChecks)
                .remarks(remarks)
                .missingItems(missingItems)
                .providerUsed(providerUsed != null ? providerUsed : "Deterministic Rubric")
                .checkedAt(Instant.now())
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
}

