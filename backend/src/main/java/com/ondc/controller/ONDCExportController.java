package com.ondc.controller;

import com.ondc.dto.response.OndcBecknCatalogResponse;
import com.ondc.service.ONDCExportService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ondc")
@RequiredArgsConstructor
public class ONDCExportController {

    private final ONDCExportService ondcExportService;

    @GetMapping("/export/{sellerId}")
    public ResponseEntity<OndcBecknCatalogResponse> exportCatalog(@PathVariable String sellerId) {
        return ResponseEntity.ok(ondcExportService.exportBecknCatalog(sellerId));
    }

    @GetMapping("/export/my")
    public ResponseEntity<OndcBecknCatalogResponse> exportMyCatalog(Principal principal) {
        String sellerId = principal.getName();
        return ResponseEntity.ok(ondcExportService.exportBecknCatalog(sellerId));
    }
}

