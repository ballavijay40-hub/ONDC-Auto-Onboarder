package com.ondc.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiGenerateRequest {

    @NotBlank(message = "Product name or brief description is required")
    private String productName;

    private String categoryHint;
    private String brand;
    private String gender;
    private String color;
    private String material;
    private String imageUrl;
    private String provider; // "gemini", "claude", or null for auto
}

