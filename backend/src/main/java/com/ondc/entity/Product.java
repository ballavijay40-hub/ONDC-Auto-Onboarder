package com.ondc.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "products")
public class Product {

    @Id
    private String id;

    @Indexed
    private String sellerId;

    private String productName;
    private String description;
    private String category;
    private List<String> tags = new ArrayList<>();
    private String imagePath;
    private String imageContentType;
    private BigDecimal price;
    private String brand;
    private String color;
    private String size;
    private String gender;
    private String material;
    private ProductSource source = ProductSource.MANUAL;
    private Instant createdAt;
    private Instant updatedAt;
}
