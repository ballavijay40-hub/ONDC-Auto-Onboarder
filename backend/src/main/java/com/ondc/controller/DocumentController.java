package com.ondc.controller;

import com.ondc.dto.response.DocumentResponse;
import com.ondc.entity.DocumentType;
import com.ondc.service.DocumentService;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/document")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") DocumentType documentType,
            Principal principal) {
        String sellerId = principal.getName();
        DocumentResponse response = documentService.uploadDocument(sellerId, documentType, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{sellerId}")
    public ResponseEntity<List<DocumentResponse>> getDocumentsBySeller(@PathVariable String sellerId) {
        return ResponseEntity.ok(documentService.getDocumentsBySeller(sellerId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<DocumentResponse>> getMyDocuments(Principal principal) {
        String sellerId = principal.getName();
        return ResponseEntity.ok(documentService.getDocumentsBySeller(sellerId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable String id, Principal principal) {
        String sellerId = principal.getName();
        documentService.deleteDocument(id, sellerId);
        return ResponseEntity.noContent().build();
    }
}

