package com.ondc.service;

import com.ondc.ai.AiServiceRouter;
import com.ondc.dto.response.ComplianceResponse;
import com.ondc.entity.ComplianceRecord;
import com.ondc.entity.Product;
import com.ondc.entity.Seller;
import com.ondc.entity.UploadedDocument;
import com.ondc.exception.ResourceNotFoundException;
import com.ondc.repository.ComplianceRepository;
import com.ondc.repository.DocumentRepository;
import com.ondc.repository.ProductRepository;
import com.ondc.repository.SellerRepository;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComplianceService {

    private final ComplianceRepository complianceRepository;
    private final SellerRepository sellerRepository;
    private final DocumentRepository documentRepository;
    private final ProductRepository productRepository;
    private final AiServiceRouter aiServiceRouter;

    public ComplianceResponse checkCompliance(String sellerId, String preferredProvider) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found: " + sellerId));

        List<UploadedDocument> documents = documentRepository.findBySellerId(sellerId);
        List<Product> products = productRepository.findBySellerId(sellerId);

        ComplianceResponse response = aiServiceRouter.auditCompliance(seller, documents, products, preferredProvider);

        ComplianceRecord record = complianceRepository.findTopBySellerIdOrderByCheckedAtDesc(sellerId)
                .orElseGet(ComplianceRecord::new);

        record.setSellerId(sellerId);
        record.setScore(response.getScore());
        record.setStatus(response.getStatus());
        record.setRemarks(response.getRemarks());
        record.setMissingItems(response.getMissingItems());
        record.setCheckedAt(Instant.now());

        ComplianceRecord saved = complianceRepository.save(record);
        response.setId(saved.getId());
        log.info("Saved compliance check for seller {}: score={}, status={}", sellerId, saved.getScore(), saved.getStatus());

        return response;
    }

    public ComplianceResponse getLatestCompliance(String sellerId) {
        return complianceRepository.findTopBySellerIdOrderByCheckedAtDesc(sellerId)
                .map(r -> ComplianceResponse.builder()
                        .id(r.getId())
                        .sellerId(r.getSellerId())
                        .score(r.getScore())
                        .status(r.getStatus())
                        .remarks(r.getRemarks())
                        .missingItems(r.getMissingItems())
                        .checkedAt(r.getCheckedAt())
                        .build())
                .orElseGet(() -> checkCompliance(sellerId, null));
    }
}
