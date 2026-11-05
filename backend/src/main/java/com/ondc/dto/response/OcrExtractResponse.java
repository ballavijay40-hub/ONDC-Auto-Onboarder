package com.ondc.dto.response;

import com.ondc.entity.DocumentType;
import com.ondc.entity.OcrStatus;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcrExtractResponse {
    private String documentId;
    private String sellerId;
    private DocumentType documentType;
    private OcrStatus status;
    private String businessName;
    private String gstNumber;
    private String panNumber;
    private String address;
    private String rawText;
    private Map<String, String> extractedFields;
    private boolean profileUpdated;
}

