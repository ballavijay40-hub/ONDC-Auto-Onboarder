package com.ondc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ondc.ai.ComplianceAI;
import com.ondc.dto.response.ComplianceResponse;
import com.ondc.entity.ComplianceRecord;
import com.ondc.entity.DocumentType;
import com.ondc.entity.OcrStatus;
import com.ondc.entity.Product;
import com.ondc.entity.Seller;
import com.ondc.entity.UploadedDocument;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ComplianceAITest {

    private ComplianceAI complianceAI;

    @BeforeEach
    void setUp() {
        complianceAI = new ComplianceAI(new ObjectMapper());
    }

    @Test
    void testComplianceNeedsAttentionWhenEmpty() {
        Seller seller = new Seller();
        seller.setId("s1");
        seller.setBusinessName("Test Store");

        ComplianceResponse response = complianceAI.evaluateCompliance(
                seller, List.of(), List.of(), null, "Test"
        );

        assertNotNull(response);
        assertTrue(response.getScore() < 80);
        assertEquals(ComplianceRecord.NEEDS_ATTENTION, response.getStatus());
        assertFalse(response.getMissingItems().isEmpty());
    }

    @Test
    void testComplianceReadyWhenAllAttributesPresent() {
        Seller seller = new Seller();
        seller.setId("s1");
        seller.setBusinessName("ABC Fashion Retail");
        seller.setOwnerName("Ramesh Kumar");
        seller.setEmail("ramesh@abc.com");
        seller.setPhone("9876543210");
        seller.setGstNumber("29ABCDE1234A1Z5");
        seller.setPanNumber("ABCDE1234A");
        seller.setAddress("123, MG Road, Indiranagar, Bangalore, 560038");

        UploadedDocument docGst = new UploadedDocument();
        docGst.setDocumentType(DocumentType.GST);
        docGst.setOcrStatus(OcrStatus.COMPLETED);

        UploadedDocument docPan = new UploadedDocument();
        docPan.setDocumentType(DocumentType.PAN);
        docPan.setOcrStatus(OcrStatus.COMPLETED);

        Product product = new Product();
        product.setProductName("Slim Fit Oxford Cotton Shirt");
        product.setDescription("Premium 100% combed cotton shirt with classic cutaway collar and breathable weave.");
        product.setCategory("Apparel & Accessories > Tops > Shirts");
        product.setPrice(new BigDecimal("1299.00"));
        product.setMaterial("100% Cotton");
        product.setImagePath("uploads/shirt.jpg");

        ComplianceResponse response = complianceAI.evaluateCompliance(
                seller, List.of(docGst, docPan), List.of(product), null, "Test"
        );

        assertNotNull(response);
        assertTrue(response.getScore() >= 80, "Expected score >= 80 but got: " + response.getScore());
        assertEquals(ComplianceRecord.READY, response.getStatus());
        assertFalse(response.getPassedChecks().isEmpty());
    }
}

