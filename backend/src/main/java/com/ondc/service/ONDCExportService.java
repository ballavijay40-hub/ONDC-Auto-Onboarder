package com.ondc.service;

import com.ondc.dto.response.ComplianceResponse;
import com.ondc.dto.response.OndcBecknCatalogResponse;
import com.ondc.dto.response.SellerResponse;
import com.ondc.entity.Product;
import com.ondc.repository.ProductRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ONDCExportService {

    private final SellerService sellerService;
    private final ProductRepository productRepository;
    private final ComplianceService complianceService;

    public OndcBecknCatalogResponse exportBecknCatalog(String sellerId) {
        SellerResponse seller = sellerService.getSellerById(sellerId);
        List<Product> products = productRepository.findBySellerId(sellerId);
        ComplianceResponse compliance = complianceService.getLatestCompliance(sellerId);

        Map<String, Object> provider = new LinkedHashMap<>();
        provider.put("id", seller.getId());
        provider.put("descriptor", Map.of(
                "name", seller.getBusinessName() != null ? seller.getBusinessName() : "ONDC Merchant",
                "short_desc", "Fashion merchant registered on ONDC Network",
                "phone", seller.getPhone() != null ? seller.getPhone() : "",
                "email", seller.getEmail() != null ? seller.getEmail() : ""
        ));
        provider.put("address", Map.of(
                "full_address", seller.getAddress() != null ? seller.getAddress() : "",
                "country", "IND"
        ));
        provider.put("tax_id", seller.getGstNumber() != null ? seller.getGstNumber() : "");
        provider.put("pan", seller.getPanNumber() != null ? seller.getPanNumber() : "");

        List<Map<String, Object>> items = new ArrayList<>();
        for (Product p : products) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", p.getId());
            item.put("descriptor", Map.of(
                    "name", p.getProductName() != null ? p.getProductName() : "",
                    "short_desc", p.getDescription() != null ? p.getDescription() : "",
                    "images", p.getImagePath() != null ? List.of(p.getImagePath()) : List.of()
            ));
            item.put("price", Map.of(
                    "currency", "INR",
                    "value", p.getPrice() != null ? p.getPrice().toString() : "0.00"
            ));
            item.put("category_id", p.getCategory() != null ? p.getCategory() : "Apparel & Accessories");
            item.put("tags", Map.of(
                    "brand", p.getBrand() != null ? p.getBrand() : "",
                    "color", p.getColor() != null ? p.getColor() : "",
                    "size", p.getSize() != null ? p.getSize() : "",
                    "gender", p.getGender() != null ? p.getGender() : "",
                    "material", p.getMaterial() != null ? p.getMaterial() : "",
                    "keywords", p.getTags() != null ? p.getTags() : List.of()
            ));
            items.add(item);
        }

        Map<String, Object> compSummary = Map.of(
                "score", compliance != null ? compliance.getScore() : 0,
                "status", compliance != null ? compliance.getStatus() : "Needs Attention",
                "networkReady", compliance != null && compliance.getScore() >= 80
        );

        return OndcBecknCatalogResponse.builder()
                .schemaVersion("1.2.0")
                .domain("ONDC:RET12")
                .exportedAt(Instant.now())
                .provider(provider)
                .items(items)
                .complianceSummary(compSummary)
                .build();
    }
}
