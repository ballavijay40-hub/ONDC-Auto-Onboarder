package com.ondc.controller;

import com.ondc.dto.request.OcrExtractRequest;
import com.ondc.dto.response.OcrExtractResponse;
import com.ondc.service.OCRService;
import com.ondc.util.FileUploadUtil;
import com.ondc.util.RegexUtil;
import jakarta.validation.Valid;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ocr")
@RequiredArgsConstructor
public class OCRController {

    private final OCRService ocrService;

    @PostMapping("/extract")
    public ResponseEntity<OcrExtractResponse> extractFromDocument(
            @Valid @RequestBody OcrExtractRequest request,
            Principal principal) {
        String sellerId = principal != null ? principal.getName() : null;
        OcrExtractResponse response = ocrService.processDocumentOcr(request.getDocumentId(), sellerId);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/extract-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OcrExtractResponse> extractFromDirectFile(
            @RequestParam("file") MultipartFile file) {
        String savedPath = FileUploadUtil.saveFile("uploads", "temp", file);
        File savedFile = Paths.get(savedPath).toFile();

        String rawText = ocrService.extractText(savedFile);
        Map<String, String> fields = RegexUtil.extractAllFields(rawText);

        OcrExtractResponse response = OcrExtractResponse.builder()
                .documentId(savedPath)
                .status(rawText != null && !rawText.isBlank() ? com.ondc.entity.OcrStatus.COMPLETED : com.ondc.entity.OcrStatus.PENDING)
                .businessName(fields.get("businessName"))
                .gstNumber(fields.get("gstNumber"))
                .panNumber(fields.get("panNumber"))
                .address(fields.get("address"))
                .rawText(rawText)
                .extractedFields(fields)
                .profileUpdated(false)
                .build();

        return ResponseEntity.ok(response);
    }
}
