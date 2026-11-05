package com.ondc.dto.response;

import com.ondc.entity.ProductSource;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private String id;
    private String sellerId;
    private String productName;
    private String description;
    private String category;
    private List<String> tags;
    private String imagePath;
    private String imageContentType;
    private BigDecimal price;
    private String brand;
    private String color;
    private String size;
    private String gender;
    private String material;
    private ProductSource source;
    private Instant createdAt;
    private Instant updatedAt;
}
