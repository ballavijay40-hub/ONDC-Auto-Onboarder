package com.ondc.service;

import com.ondc.config.AppProperties;
import com.ondc.dto.response.OcrExtractResponse;
import com.ondc.entity.OcrStatus;
import com.ondc.entity.UploadedDocument;
import com.ondc.exception.ResourceNotFoundException;
import com.ondc.repository.DocumentRepository;
import com.ondc.util.OCRUtil;
import com.ondc.util.RegexUtil;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OCRService {

    private final AppProperties appProperties;
    private final DocumentRepository documentRepository;
    private final SellerService sellerService;

    private OCRUtil ocrUtil;

    @PostConstruct
    public void init() {
        this.ocrUtil = new OCRUtil(appProperties.getTesseract().getDataPath());
    }

    public String extractText(File file) {
        return ocrUtil.extractTextFromFile(file);
    }

    public OcrExtractResponse processDocumentOcr(String documentId, String sellerId) {
        UploadedDocument doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));

        if (sellerId != null && !sellerId.equals(doc.getSellerId())) {
            throw new ResourceNotFoundException("Document not found for current seller");
        }

        File file = Paths.get(doc.getFilePath()).toFile();
        if (!file.exists()) {
            doc.setOcrStatus(OcrStatus.FAILED);
            documentRepository.save(doc);
            throw new ResourceNotFoundException("Physical file not found at path: " + doc.getFilePath());
        }

        String rawText = "";
        try {
            rawText = ocrUtil.extractTextFromFile(file);
        } catch (Exception ex) {
            log.error("OCR execution failed for file: {}", file.getName(), ex);
        }

        Map<String, String> extractedFields = RegexUtil.extractAllFields(rawText);
        String gstNumber = extractedFields.get("gstNumber");
        String panNumber = extractedFields.get("panNumber");
        String businessName = extractedFields.get("businessName");
        String address = extractedFields.get("address");

        doc.setOcrText(rawText);
        doc.setExtractedFields(extractedFields);
        doc.setOcrStatus(rawText != null && !rawText.isBlank() ? OcrStatus.COMPLETED : OcrStatus.PENDING);
        documentRepository.save(doc);

        boolean updated = false;
        if (doc.getSellerId() != null) {
            sellerService.updateSellerFromOcr(doc.getSellerId(), gstNumber, panNumber, businessName, address);
            updated = true;
        }

        return OcrExtractResponse.builder()
                .documentId(doc.getId())
                .sellerId(doc.getSellerId())
                .documentType(doc.getDocumentType())
                .status(doc.getOcrStatus())
                .businessName(businessName)
                .gstNumber(gstNumber)
                .panNumber(panNumber)
                .address(address)
                .rawText(rawText)
                .extractedFields(extractedFields)
                .profileUpdated(updated)
                .build();
    }
}

