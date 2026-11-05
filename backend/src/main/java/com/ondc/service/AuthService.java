package com.ondc.service;

import com.ondc.config.JwtTokenProvider;
import com.ondc.dto.request.LoginRequest;
import com.ondc.dto.request.RegisterRequest;
import com.ondc.dto.response.AuthResponse;
import com.ondc.dto.response.SellerResponse;
import com.ondc.entity.Seller;
import com.ondc.exception.BadRequestException;
import com.ondc.exception.UnauthorizedException;
import com.ondc.repository.SellerRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        if (sellerRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("An account with email " + normalizedEmail + " already exists");
        }

        Seller seller = new Seller();
        seller.setBusinessName(request.getBusinessName().trim());
        seller.setOwnerName(request.getOwnerName().trim());
        seller.setEmail(normalizedEmail);
        seller.setPhone(request.getPhone().trim());
        seller.setAddress(request.getAddress() != null ? request.getAddress().trim() : "");
        seller.setGstNumber(request.getGstNumber() != null ? request.getGstNumber().trim().toUpperCase() : null);
        seller.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        seller.setRole(Seller.ROLE_SELLER);
        seller.setCreatedAt(Instant.now());

        Seller saved = sellerRepository.save(seller);
        String token = jwtTokenProvider.generateToken(saved);

        log.info("Registered new seller: {} ({})", saved.getBusinessName(), saved.getEmail());
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .seller(toSellerResponse(saved))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        Seller seller = sellerRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), seller.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateToken(seller);
        log.info("Seller logged in: {}", seller.getEmail());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .seller(toSellerResponse(seller))
                .build();
    }

    public SellerResponse toSellerResponse(Seller seller) {
        return SellerResponse.builder()
                .id(seller.getId())
                .businessName(seller.getBusinessName())
                .ownerName(seller.getOwnerName())
                .email(seller.getEmail())
                .phone(seller.getPhone())
                .address(seller.getAddress())
                .gstNumber(seller.getGstNumber())
                .panNumber(seller.getPanNumber())
                .role(seller.getRole())
                .createdAt(seller.getCreatedAt())
                .build();
    }
}
