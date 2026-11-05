package com.ondc.service;

import com.ondc.config.AppProperties;
import com.ondc.dto.response.DocumentResponse;
import com.ondc.entity.DocumentType;
import com.ondc.entity.OcrStatus;
import com.ondc.entity.UploadedDocument;
import com.ondc.exception.ResourceNotFoundException;
import com.ondc.repository.DocumentRepository;
import com.ondc.util.FileUploadUtil;
import java.io.File;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final AppProperties appProperties;
    private final OCRService ocrService;

    public DocumentResponse uploadDocument(String sellerId, DocumentType documentType, MultipartFile file) {
        String savedPath = FileUploadUtil.saveFile(
                appProperties.getUpload().getDir(),
                "documents/" + sellerId,
                file
        );

        Optional<UploadedDocument> existingOpt =
                documentRepository.findBySellerIdAndDocumentType(sellerId, documentType);

        UploadedDocument doc = existingOpt.orElseGet(UploadedDocument::new);
        doc.setSellerId(sellerId);
        doc.setDocumentType(documentType);
        doc.setFilePath(savedPath);
        doc.setOriginalFileName(file.getOriginalFilename());
        doc.setContentType(file.getContentType());
        doc.setOcrStatus(OcrStatus.PENDING);
        doc.setUploadedAt(Instant.now());

        UploadedDocument saved = documentRepository.save(doc);
        log.info("Saved document {} for seller {}", saved.getId(), sellerId);

        // Automatically trigger OCR processing
        try {
            ocrService.processDocumentOcr(saved.getId(), sellerId);
            // Re-fetch to return with OCR status and fields
            saved = documentRepository.findById(saved.getId()).orElse(saved);
        } catch (Exception ex) {
            log.warn("Automatic OCR failed upon upload (can be retriggered): {}", ex.getMessage());
        }

        return toResponse(saved);
    }

    public List<DocumentResponse> getDocumentsBySeller(String sellerId) {
        return documentRepository.findBySellerId(sellerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public DocumentResponse getDocumentById(String id) {
        UploadedDocument doc = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));
        return toResponse(doc);
    }

    public void deleteDocument(String id, String sellerId) {
        UploadedDocument doc = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));
        if (sellerId != null && !sellerId.equals(doc.getSellerId())) {
            throw new ResourceNotFoundException("Document not found for current seller");
        }
        try {
            File physicalFile = new File(doc.getFilePath());
            if (physicalFile.exists()) {
                physicalFile.delete();
            }
        } catch (Exception e) {
            log.warn("Failed to delete physical file: {}", doc.getFilePath());
        }
        documentRepository.delete(doc);
    }

    public DocumentResponse toResponse(UploadedDocument doc) {
        return DocumentResponse.builder()
                .id(doc.getId())
                .sellerId(doc.getSellerId())
                .documentType(doc.getDocumentType())
                .filePath(doc.getFilePath())
                .originalFileName(doc.getOriginalFileName())
                .contentType(doc.getContentType())
                .ocrText(doc.getOcrText())
                .extractedFields(doc.getExtractedFields())
                .ocrStatus(doc.getOcrStatus())
                .uploadedAt(doc.getUploadedAt())
                .build();
    }
}

