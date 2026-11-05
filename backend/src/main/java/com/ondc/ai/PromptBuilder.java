package com.ondc.ai;

import com.ondc.dto.request.AiGenerateRequest;
import com.ondc.entity.Product;
import com.ondc.entity.Seller;
import com.ondc.entity.UploadedDocument;
import java.util.List;

public final class PromptBuilder {

    private PromptBuilder() {}

    public static String buildCatalogPrompt(AiGenerateRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are an expert e-commerce catalog specialist for the Open Network for Digital Commerce (ONDC) in India, specializing in Fashion and Apparel.\n\n");
        sb.append("Generate an ONDC-compliant product catalog entry for the following fashion item:\n");
        sb.append("- Product Name / Brief: ").append(request.getProductName()).append("\n");
        if (request.getCategoryHint() != null && !request.getCategoryHint().isBlank()) {
            sb.append("- Category Hint: ").append(request.getCategoryHint()).append("\n");
        }
        if (request.getBrand() != null && !request.getBrand().isBlank()) {
            sb.append("- Brand: ").append(request.getBrand()).append("\n");
        }
        if (request.getGender() != null && !request.getGender().isBlank()) {
            sb.append("- Gender Target: ").append(request.getGender()).append("\n");
        }
        if (request.getColor() != null && !request.getColor().isBlank()) {
            sb.append("- Color: ").append(request.getColor()).append("\n");
        }
        if (request.getMaterial() != null && !request.getMaterial().isBlank()) {
            sb.append("- Fabric / Material: ").append(request.getMaterial()).append("\n");
        }

        sb.append("\nReturn ONLY a valid JSON object with no markdown formatting, no code fences, and no surrounding text. The JSON must have these exact keys:\n");
        sb.append("{\n");
        sb.append("  \"title\": \"[Standardized descriptive product title under 80 characters]\",\n");
        sb.append("  \"description\": \"[Rich SEO-friendly description including style, fit, occasion, weave, and highlights, 50-120 words]\",\n");
        sb.append("  \"category\": \"[Standard ONDC Apparel Category, e.g., 'Apparel & Accessories > Men > Shirts']\",\n");
        sb.append("  \"brand\": \"[Brand name]\",\n");
        sb.append("  \"color\": \"[Dominant color]\",\n");
        sb.append("  \"size\": \"[Standard size, e.g., M, L, XL, Free Size]\",\n");
        sb.append("  \"gender\": \"[Men, Women, Unisex, Boys, Girls]\",\n");
        sb.append("  \"material\": \"[Fabric details, e.g., 100% Cotton, Linen Blend, Polyester]\",\n");
        sb.append("  \"careInstructions\": \"[Standard care instructions]\",\n");
        sb.append("  \"tags\": [\"fashion\", \"apparel\", \"tag1\", \"tag2\", \"tag3\", \"tag4\"],\n");
        sb.append("  \"highlights\": [\"Feature 1\", \"Feature 2\", \"Feature 3\"]\n");
        sb.append("}\n");

        return sb.toString();
    }

    public static String buildComplianceAuditPrompt(Seller seller, List<UploadedDocument> documents, List<Product> products) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are an ONDC Network Compliance Auditor evaluating a Fashion & Apparel merchant for live network onboarding.\n\n");
        sb.append("Evaluate this seller against Indian E-Commerce and ONDC standards:\n");
        sb.append("1. Legal Metrology (Packaged Commodities) Rules: MRP, country of origin, size, color, material composition.\n");
        sb.append("2. Consumer Protection (E-Commerce) Rules 2020: Verified GSTIN, PAN, registered business address, customer support contact.\n");
        sb.append("3. ONDC Catalog completeness: Product description length (>30 chars), valid categories, image references.\n\n");

        sb.append("Seller Information:\n");
        sb.append("- Business Name: ").append(seller.getBusinessName()).append("\n");
        sb.append("- Owner Name: ").append(seller.getOwnerName()).append("\n");
        sb.append("- Email: ").append(seller.getEmail()).append("\n");
        sb.append("- Phone: ").append(seller.getPhone()).append("\n");
        sb.append("- GSTIN: ").append(seller.getGstNumber() != null ? seller.getGstNumber() : "NOT_PROVIDED").append("\n");
        sb.append("- PAN: ").append(seller.getPanNumber() != null ? seller.getPanNumber() : "NOT_PROVIDED").append("\n");
        sb.append("- Address: ").append(seller.getAddress() != null ? seller.getAddress() : "NOT_PROVIDED").append("\n\n");

        sb.append("Uploaded Documents (").append(documents.size()).append(" total):\n");
        for (UploadedDocument doc : documents) {
            sb.append("- Type: ").append(doc.getDocumentType())
                    .append(" | OCR Status: ").append(doc.getOcrStatus())
                    .append(" | Has Extracted Fields: ").append(!doc.getExtractedFields().isEmpty()).append("\n");
        }
        sb.append("\n");

        sb.append("Product Catalog (").append(products.size()).append(" items):\n");
        for (Product p : products) {
            sb.append("- Item: ").append(p.getProductName())
                    .append(" | Category: ").append(p.getCategory())
                    .append(" | Material: ").append(p.getMaterial())
                    .append(" | Price: ").append(p.getPrice())
                    .append(" | Has Image: ").append(p.getImagePath() != null).append("\n");
        }
        sb.append("\n");

        sb.append("Return ONLY a valid JSON object with no markdown formatting, no code fences, and no extra text:\n");
        sb.append("{\n");
        sb.append("  \"score\": [Integer between 0 and 100],\n");
        sb.append("  \"status\": \"['ONDC Ready' if score >= 80 else 'Needs Attention']\",\n");
        sb.append("  \"passedChecks\": [\"check 1\", \"check 2\", \"check 3\"],\n");
        sb.append("  \"remarks\": [\"remark 1\", \"remark 2\"],\n");
        sb.append("  \"missingItems\": [\"missing item 1\", \"missing item 2\"]\n");
        sb.append("}\n");

        return sb.toString();
    }
}

