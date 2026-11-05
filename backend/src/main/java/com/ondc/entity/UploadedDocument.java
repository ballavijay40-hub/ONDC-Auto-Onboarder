package com.ondc.entity;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "documents")
@CompoundIndex(name = "seller_document_type", def = "{'sellerId': 1, 'documentType': 1}", unique = true)
public class UploadedDocument {

    @Id
    private String id;

    private String sellerId;
    private DocumentType documentType;
    private String filePath;
    private String originalFileName;
    private String contentType;
    private String ocrText;
    private Map<String, String> extractedFields = new LinkedHashMap<>();
    private OcrStatus ocrStatus = OcrStatus.PENDING;
    private Instant uploadedAt;
}
