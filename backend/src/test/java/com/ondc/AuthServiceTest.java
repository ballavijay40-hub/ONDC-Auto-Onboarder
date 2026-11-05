package com.ondc;

import com.ondc.config.AppProperties;
import com.ondc.config.JwtTokenProvider;
import com.ondc.dto.response.SellerResponse;
import com.ondc.entity.Seller;
import com.ondc.service.AuthService;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private AuthService authService;
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        AppProperties appProperties = new AppProperties();
        jwtTokenProvider = new JwtTokenProvider(appProperties);
        jwtTokenProvider.init();
        authService = new AuthService(null, null, jwtTokenProvider);
    }

    @Test
    void testToSellerResponseMapping() {
        Seller seller = new Seller();
        seller.setId("s-123");
        seller.setBusinessName("Fabrics & Silks");
        seller.setOwnerName("Meera Patel");
        seller.setEmail("meera@fabrics.com");
        seller.setPhone("9876543210");
        seller.setAddress("Bangalore, Karnataka");
        seller.setGstNumber("29ABCDE1234A1Z5");
        seller.setPanNumber("ABCDE1234A");
        seller.setRole(Seller.ROLE_SELLER);
        seller.setCreatedAt(Instant.now());

        SellerResponse res = authService.toSellerResponse(seller);

        assertNotNull(res);
        assertEquals("s-123", res.getId());
        assertEquals("Fabrics & Silks", res.getBusinessName());
        assertEquals("meera@fabrics.com", res.getEmail());
        assertEquals("29ABCDE1234A1Z5", res.getGstNumber());
    }

    @Test
    void testJwtTokenGenerationAndValidation() {
        Seller seller = new Seller();
        seller.setId("seller-abc");
        seller.setEmail("seller@apparel.com");
        seller.setBusinessName("Apparel House");
        seller.setRole(Seller.ROLE_SELLER);

        String token = jwtTokenProvider.generateToken(seller);
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals("seller-abc", jwtTokenProvider.getSellerIdFromToken(token));
        assertEquals("seller@apparel.com", jwtTokenProvider.getEmailFromToken(token));
    }
}
