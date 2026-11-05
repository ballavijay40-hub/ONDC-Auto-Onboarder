package com.ondc.service;

import com.ondc.dto.response.ComplianceResponse;
import com.ondc.dto.response.DashboardResponse;
import com.ondc.dto.response.SellerResponse;
import com.ondc.entity.DocumentType;
import com.ondc.entity.OcrStatus;
import com.ondc.entity.Product;
import com.ondc.entity.ProductSource;
import com.ondc.entity.UploadedDocument;
import com.ondc.repository.DocumentRepository;
import com.ondc.repository.ProductRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SellerService sellerService;
    private final DocumentRepository documentRepository;
    private final ProductRepository productRepository;
    private final ComplianceService complianceService;

    public DashboardResponse getDashboardStats(String sellerId) {
        SellerResponse seller = sellerService.getSellerById(sellerId);
        List<UploadedDocument> documents = documentRepository.findBySellerId(sellerId);
        List<Product> products = productRepository.findBySellerId(sellerId);

        long verifiedDocuments = documents.stream()
                .filter(d -> d.getOcrStatus() == OcrStatus.COMPLETED)
                .count();

        long aiProducts = products.stream()
                .filter(p -> p.getSource() == ProductSource.AI)
                .count();

        ComplianceResponse compliance = complianceService.getLatestCompliance(sellerId);

        Map<String, Boolean> funnel = new LinkedHashMap<>();
        funnel.put("sellerRegistered", true);
        funnel.put("documentsUploaded", !documents.isEmpty());
        funnel.put("ocrExtracted", verifiedDocuments > 0);
        funnel.put("productsUploaded", !products.isEmpty());
        funnel.put("aiCatalogGenerated", aiProducts > 0);
        funnel.put("complianceChecked", compliance != null && compliance.getScore() > 0);
        funnel.put("ondcReady", compliance != null && compliance.getScore() >= 80);

        List<String> pending = new ArrayList<>();
        if (seller.getGstNumber() == null || seller.getGstNumber().isBlank()) {
            pending.add("Upload GST certificate to verify GSTIN");
        }
        if (documents.stream().noneMatch(d -> d.getDocumentType() == DocumentType.PAN)) {
            pending.add("Upload PAN card for KYC verification");
        }
        if (products.isEmpty()) {
            pending.add("Upload at least 1 product or generate catalog via AI");
        }
        if (compliance != null && compliance.getScore() < 80) {
            pending.addAll(compliance.getMissingItems());
        }

        return DashboardResponse.builder()
                .seller(seller)
                .totalDocuments(documents.size())
                .verifiedDocuments(verifiedDocuments)
                .totalProducts(products.size())
                .aiGeneratedProducts(aiProducts)
                .complianceScore(compliance != null ? compliance.getScore() : 0)
                .complianceStatus(compliance != null ? compliance.getStatus() : "Needs Attention")
                .ondcReady(compliance != null && compliance.getScore() >= 80)
                .pendingActionItems(pending)
                .onboardingFunnel(funnel)
                .build();
    }
}

