package com.ondc;

import com.ondc.dto.response.ComplianceResponse;
import com.ondc.dto.response.OndcBecknCatalogResponse;
import com.ondc.dto.response.SellerResponse;
import com.ondc.entity.ComplianceRecord;
import com.ondc.entity.Product;
import com.ondc.repository.ProductRepository;
import com.ondc.service.ComplianceService;
import com.ondc.service.ONDCExportService;
import com.ondc.service.SellerService;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ONDCExportServiceTest {

    @Test
    void testExportBecknCatalogStructure() {
        String sellerId = "sel-101";

        SellerResponse seller = SellerResponse.builder()
                .id(sellerId)
                .businessName("Silk Sarees Emporium")
                .ownerName("Priya Sharma")
                .email("priya@sarees.com")
                .phone("9876543210")
                .gstNumber("29ABCDE1234A1Z5")
                .panNumber("ABCDE1234A")
                .address("MG Road, Bangalore")
                .build();

        Product product = new Product();
        product.setId("prod-1");
        product.setProductName("Kanjeevaram Pure Silk Saree");
        product.setDescription("Traditional handloom pure silk saree with zari border.");
        product.setCategory("Apparel & Accessories > Ethnic Wear > Sarees");
        product.setPrice(new BigDecimal("8499.00"));
        product.setColor("Crimson Red");
        product.setMaterial("Pure Silk");

        ComplianceResponse compliance = ComplianceResponse.builder()
                .sellerId(sellerId)
                .score(95)
                .status(ComplianceRecord.READY)
                .build();

        // Use lightweight subclass stubs without dynamic ByteBuddy agent attachment
        SellerService stubSellerService = new SellerService(null, null) {
            @Override
            public SellerResponse getSellerById(String id) {
                return seller;
            }
        };

        ProductRepository stubProductRepo = (ProductRepository) Proxy.newProxyInstance(
                ProductRepository.class.getClassLoader(),
                new Class<?>[]{ProductRepository.class},
                (proxy, method, args) -> {
                    if ("findBySellerId".equals(method.getName())) {
                        return List.of(product);
                    }
                    return null;
                }
        );

        ComplianceService stubComplianceService = new ComplianceService(null, null, null, null, null) {
            @Override
            public ComplianceResponse getLatestCompliance(String id) {
                return compliance;
            }
        };

        ONDCExportService exportService = new ONDCExportService(stubSellerService, stubProductRepo, stubComplianceService);

        OndcBecknCatalogResponse response = exportService.exportBecknCatalog(sellerId);

        assertNotNull(response);
        assertEquals("1.2.0", response.getSchemaVersion());
        assertEquals("ONDC:RET12", response.getDomain());
        assertNotNull(response.getProvider());
        assertEquals(1, response.getItems().size());
        assertEquals("Kanjeevaram Pure Silk Saree", ((java.util.Map<?, ?>) response.getItems().get(0).get("descriptor")).get("name"));
        assertTrue((Boolean) response.getComplianceSummary().get("networkReady"));
    }
}
