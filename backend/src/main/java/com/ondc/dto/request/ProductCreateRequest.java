package com.ondc.dto.request;

import com.ondc.entity.ProductSource;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequest {

    @NotBlank(message = "Product name is required")
    private String productName;

    private String description;
    private String category;
    private BigDecimal price;
    private String brand;
    private String color;
    private String size;
    private String gender;
    private String material;
    private String imagePath;
    private String imageContentType;

    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @Builder.Default
    private ProductSource source = ProductSource.MANUAL;
}

