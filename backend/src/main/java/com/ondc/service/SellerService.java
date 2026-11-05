package com.ondc.service;

import com.ondc.dto.request.SellerUpdateRequest;
import com.ondc.dto.response.SellerResponse;
import com.ondc.entity.Seller;
import com.ondc.exception.ResourceNotFoundException;
import com.ondc.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;
    private final AuthService authService;

    public Seller findEntityById(String id) {
        return sellerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with id: " + id));
    }

    public SellerResponse getSellerById(String id) {
        return authService.toSellerResponse(findEntityById(id));
    }

    public SellerResponse updateSeller(String id, SellerUpdateRequest request) {
        Seller seller = findEntityById(id);

        if (request.getBusinessName() != null && !request.getBusinessName().isBlank()) {
            seller.setBusinessName(request.getBusinessName().trim());
        }
        if (request.getOwnerName() != null && !request.getOwnerName().isBlank()) {
            seller.setOwnerName(request.getOwnerName().trim());
        }
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            seller.setPhone(request.getPhone().trim());
        }
        if (request.getAddress() != null) {
            seller.setAddress(request.getAddress().trim());
        }
        if (request.getGstNumber() != null && !request.getGstNumber().isBlank()) {
            seller.setGstNumber(request.getGstNumber().trim().toUpperCase());
        }
        if (request.getPanNumber() != null && !request.getPanNumber().isBlank()) {
            seller.setPanNumber(request.getPanNumber().trim().toUpperCase());
        }

        Seller saved = sellerRepository.save(seller);
        log.info("Updated seller profile: {}", saved.getId());
        return authService.toSellerResponse(saved);
    }

    public void updateSellerFromOcr(String sellerId, String gstNumber, String panNumber, String businessName, String address) {
        Seller seller = findEntityById(sellerId);
        boolean changed = false;

        if (gstNumber != null && !gstNumber.isBlank() && (seller.getGstNumber() == null || seller.getGstNumber().isBlank())) {
            seller.setGstNumber(gstNumber.trim().toUpperCase());
            changed = true;
        }
        if (panNumber != null && !panNumber.isBlank() && (seller.getPanNumber() == null || seller.getPanNumber().isBlank())) {
            seller.setPanNumber(panNumber.trim().toUpperCase());
            changed = true;
        }
        if (businessName != null && !businessName.isBlank() && (seller.getBusinessName() == null || seller.getBusinessName().isBlank())) {
            seller.setBusinessName(businessName.trim());
            changed = true;
        }
        if (address != null && !address.isBlank() && (seller.getAddress() == null || seller.getAddress().isBlank())) {
            seller.setAddress(address.trim());
            changed = true;
        }

        if (changed) {
            sellerRepository.save(seller);
            log.info("Auto-enriched seller profile from OCR: {}", sellerId);
        }
    }
}
