package com.ondc.dto.response;

import com.ondc.entity.DocumentType;
import com.ondc.entity.OcrStatus;
import java.time.Instant;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
    private String id;
    private String sellerId;
    private DocumentType documentType;
    private String filePath;
    private String originalFileName;
    private String contentType;
    private String ocrText;
    private Map<String, String> extractedFields;
    private OcrStatus ocrStatus;
    private Instant uploadedAt;
}

