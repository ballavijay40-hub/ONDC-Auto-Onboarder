package com.ondc.controller;

import com.ondc.dto.request.SellerUpdateRequest;
import com.ondc.dto.response.SellerResponse;
import com.ondc.service.SellerService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seller")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    @GetMapping("/profile")
    public ResponseEntity<SellerResponse> getProfile(Principal principal) {
        String sellerId = principal.getName();
        return ResponseEntity.ok(sellerService.getSellerById(sellerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SellerResponse> getSellerById(@PathVariable String id) {
        return ResponseEntity.ok(sellerService.getSellerById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SellerResponse> updateSeller(@PathVariable String id,
                                                       @Valid @RequestBody SellerUpdateRequest request) {
        return ResponseEntity.ok(sellerService.updateSeller(id, request));
    }

    @PostMapping
    public ResponseEntity<SellerResponse> createOrUpdateSeller(Principal principal,
                                                              @Valid @RequestBody SellerUpdateRequest request) {
        String sellerId = principal.getName();
        return ResponseEntity.ok(sellerService.updateSeller(sellerId, request));
    }
}

