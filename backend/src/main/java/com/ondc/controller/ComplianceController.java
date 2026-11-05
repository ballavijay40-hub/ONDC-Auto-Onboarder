package com.ondc.controller;

import com.ondc.dto.request.ComplianceCheckRequest;
import com.ondc.dto.response.ComplianceResponse;
import com.ondc.service.ComplianceService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/compliance")
@RequiredArgsConstructor
public class ComplianceController {

    private final ComplianceService complianceService;

    @PostMapping("/check")
    public ResponseEntity<ComplianceResponse> runComplianceCheck(
            @RequestBody(required = false) ComplianceCheckRequest request,
            Principal principal) {
        String sellerId = (request != null && request.getSellerId() != null)
                ? request.getSellerId()
                : principal.getName();
        String provider = request != null ? request.getProvider() : null;

        ComplianceResponse response = complianceService.checkCompliance(sellerId, provider);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{sellerId}")
    public ResponseEntity<ComplianceResponse> getCompliance(@PathVariable String sellerId) {
        return ResponseEntity.ok(complianceService.getLatestCompliance(sellerId));
    }

    @GetMapping("/my")
    public ResponseEntity<ComplianceResponse> getMyCompliance(Principal principal) {
        String sellerId = principal.getName();
        return ResponseEntity.ok(complianceService.getLatestCompliance(sellerId));
    }
}

